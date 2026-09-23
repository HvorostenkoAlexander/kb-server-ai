import os
import sys
import json
import time
import glob
from github import Github
from google import genai
from google.genai import types

def get_project_context():
    """Собирает файлы конфигурации и структуру проекта для определения контекста."""
    context_files = []
    key_patterns = ['pom.xml', 'build.gradle*', 'package.json', 'Dockerfile', 'README.md', 'settings.gradle*']

    for pattern in key_patterns:
        for filepath in glob.glob(pattern, recursive=True):
            if os.path.isfile(filepath) and os.path.getsize(filepath) < 50000:
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        context_files.append(f"--- FILE: {filepath} ---\n{f.read()}\n")
                except Exception:
                    pass
    return "\n".join(context_files)

def main():
    issue_number = os.getenv("ISSUE_NUMBER")
    issue_title = os.getenv("ISSUE_TITLE", "")
    issue_body = os.getenv("ISSUE_BODY", "")
    gemini_key = os.getenv("GEMINI_API_KEY")
    github_token = os.getenv("GITHUB_TOKEN")
    repo_name = os.getenv("REPO_NAME")

    if not gemini_key:
        print("❌ Ошибка: GEMINI_API_KEY не установлен.")
        sys.exit(1)

    print(f"🤖 Начинаю обработку задачи #{issue_number} в репозитории {repo_name}...")

    gh = Github(github_token)
    repo = gh.get_repo(repo_name)
    issue = repo.get_issue(int(issue_number))

    issue.create_comment("🚀 **gear-bot-dev** начал анализ задачи и подготовку решения...")

    project_context = get_project_context()

    client = genai.Client(api_key=gemini_key)

    prompt = f"""
    You are an expert Senior Software Engineer working on the repository '{repo_name}'.
    Your goal is to solve the task requested in the Issue.

    ### PROJECT CONTEXT & CONFIGURATION:
    {project_context if project_context else "No build files detected."}

    ### ISSUE DETAILS:
    Title: {issue_title}
    Description:
    {issue_body}

    ### INSTRUCTIONS:
    1. Implement the requested changes adhering strictly to existing project conventions.
    2. Output ONLY a valid JSON object without markdown formatting wrapping:
    {{
        "files": [
            {{
                "path": "relative/path/to/file.ext",
                "content": "Full content of the file"
            }}
        ],
        "summary": "Brief explanation of what was created or updated"
    }}
    """

    # --- Вызов Gemini API с повторными попытками при ошибке 503 (перегрузка сервера) ---
    response = None
    max_retries = 3
    retry_delay = 5  # пауза в секундах между попытками

    for attempt in range(1, max_retries + 1):
        try:
            print(f"📡 Запрос к Gemini API (попытка {attempt}/{max_retries})...")
            response = client.models.generate_content(
                model='gemini-1.5-flash',
                contents=prompt,
                config=types.GenerateContentConfig(
                    response_mime_type="application/json"
                )
            )
            break  # Успешно получили ответ — выходим из цикла
        except Exception as e:
            print(f"⚠️ Попытка {attempt} завершилась ошибкой: {e}")
            if attempt < max_retries:
                print(f"⏳ Ожидание {retry_delay} сек. перед повторной попыткой...")
                time.sleep(retry_delay)
            else:
                print("❌ Превышено количество попыток подключения к Gemini API.")
                sys.exit(1)

    try:
        result = json.loads(response.text)
    except Exception as e:
        print(f"❌ Ошибка парсинга JSON от AI: {e}\nОтвет: {response.text}")
        sys.exit(1)

    for file_info in result.get("files", []):
        path = file_info.get("path")
        content = file_info.get("content")
        if os.path.dirname(path):
            os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"✅ Файл обновлён/создан: {path}")

    summary = result.get("summary", "Патч успешно подготовлен.")
    issue.create_comment(f"✅ **Патч сформирован!**\n\n**Что сделано:** {summary}\n\nСоздаю Pull Request...")

if __name__ == "__main__":
    main()