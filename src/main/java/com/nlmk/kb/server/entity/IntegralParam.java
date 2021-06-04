package com.nlmk.kb.server.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * интегральные параметры еденицы продукции
 */
@Deprecated
@Data
@Entity
@Table(name = "integral_parameters")
@ToString(callSuper = true)
public class IntegralParam extends BaseParam{
}
