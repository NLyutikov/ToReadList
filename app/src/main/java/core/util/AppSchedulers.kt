package ru.appkode.base.core.util

import io.reactivex.Scheduler

/**
 * Abstracts app schedulers to allow easily mocking them in unit/integration tests
 */
interface AppSchedulers {
  val io: Scheduler
  val computation: Scheduler
  val ui: Scheduler
}

