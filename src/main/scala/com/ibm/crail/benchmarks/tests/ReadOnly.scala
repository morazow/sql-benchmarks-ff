/*
 * Crail SQL Benchmarks
 *
 * Author: Animesh Trivedi <atr@zurich.ibm.com>
 *
 * Copyright (C) 2017, IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.crail.benchmarks.tests

import com.ibm.crail.benchmarks.{ParseOptions, SQLTest}
import org.apache.spark.sql.Dataset

/**
  * Created by atr on 05.05.17.
  */
class ReadOnly(val options: ParseOptions) extends SQLTest {
  private val inputFile = options.getInputFiles()(0)
  private var readDataSet:Dataset[_] = _
  try {
    readDataSet = spark.read.parquet(inputFile)
  } catch {
    case e:org.apache.spark.sql.AnalysisException => System.err.println("-----\n "
      + "Hint: Perhaps you specified a TPC-DS directory instead of a file?\n" +
      "-----\n");
  }

  // we do cache here, because now any action will trigger the whole data set reading
  // even the count().
  override def execute(): String = takeAction(options, readDataSet.cache())

  override def explain(): Unit = readDataSet.explain(true)

  override def plainExplain(): String = "ReadOnly (with .cache()) test on " + inputFile
}