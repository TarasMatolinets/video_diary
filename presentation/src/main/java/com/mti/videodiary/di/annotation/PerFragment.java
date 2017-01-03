package com.mti.videodiary.di.annotation;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A scoping annotation to permit objects during fragment life circle
 */
@Scope
@Retention(RUNTIME)
public @interface PerFragment {
}