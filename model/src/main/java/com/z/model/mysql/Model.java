package com.z.model.mysql;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by YYL on 2018/11/25
 */
@Getter
@Setter
@MappedSuperclass
public abstract class Model extends BaseModel {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
}
