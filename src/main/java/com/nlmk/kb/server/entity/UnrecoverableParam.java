package com.nlmk.kb.server.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * не хранимые параметры трендов
 */
@Deprecated
@Data
@Entity
@Table(name = "unrecoverable_parameters")
@ToString(callSuper = true)
public class UnrecoverableParam extends BaseParam{
}
