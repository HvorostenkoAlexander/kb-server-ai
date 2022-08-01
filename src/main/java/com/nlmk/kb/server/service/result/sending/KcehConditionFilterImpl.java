package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KcehConditionFilterImpl implements CommonConditionFilter {

    @Override
    public Optional<ProductDto> filter(ProductDto product, String condition) {
        if (product == null) {
            return Optional.empty();
        }

        final var kceh = getKcehCondition(condition);

        if (kceh == null) {
            log.warn("Фильтр по kceh не применяется. Отправляются нефильтрованные данные.");
            return Optional.of(product);
        }
        product.setRequests(
                product.getRequests().stream()
                        .filter(r -> r.getKceh() != null)
                        .filter(r -> r.getKceh().equals(kceh))
                        .collect(Collectors.toList())
        );
        return Optional.of(product);
    }

    private Long getKcehCondition(String condition) {
        Long kceh = null;

        if (StringUtils.isBlank(condition)) {
            log.warn("Не установлены условия для фильтрации. condition.isBlank() : [{}]", condition);
            return null;
        }
        if (StringUtils.countMatches(condition, "=") != 1) {
            log.warn("Не корректные условия для фильтрации: [{}]", condition);
            return null;
        }
        final var rawData = StringUtils.split(condition, "=");
        log.info("condition filter rawData: [{}]", Arrays.asList(rawData));

        if (rawData.length != 2) {
            log.warn("Не установлены условия фильтрации для kceh: [{}]", condition);
            return null;
        }
        if (!StringUtils.equalsIgnoreCase(rawData[0], "kceh")) {
            log.warn("Не установлены условия фильтрации для kceh: [{}]", condition);
            return null;
        }
        if (StringUtils.isNumeric(rawData[1])) {
            try {
                kceh = Long.parseLong(rawData[1]);
            } catch (NumberFormatException nfe) {
                log.warn(nfe.toString());
                log.warn("Не корректные значения для kceh: [{}]", condition);
            }
        } else {
            log.warn("Не корректные значения для kceh: [{}]", condition);
        }
        return kceh;
    }

}
