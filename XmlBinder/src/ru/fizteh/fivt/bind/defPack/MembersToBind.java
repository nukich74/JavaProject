package ru.fizteh.fivt.bind.defPack;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public enum MembersToBind {

    /**
     * Все поля (публичные и приватные).
     */
    FIELDS,

    /**
     * Все публичные методы-пары.
     *
     * Getter - это метод, не принимающий аргументов, начинающийся с getXxx
     * или isXxx, если возвращаемый тип - boolean.
     *
     * Setter - это метод, напичающийся с setXxx, имеющий один аргумент,
     * не возвращающий ничего.
     *
     * При этом должно использоваться название со строчной буквы (т.е. первая
     * буква должна приводиться к нижнему регистру).
     */
    GETTERS_AND_SETTERS,
}