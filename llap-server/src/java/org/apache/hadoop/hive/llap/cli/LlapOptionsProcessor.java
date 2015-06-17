/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.llap.cli;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.StringUtils;

public class LlapOptionsProcessor {

  public class LlapOptions {
    private int instances = 0;
    private String directory = null;
    private String name;
    private int executors;
    private long cache;
    private long size;
    private long xmx;

    public LlapOptions(String name, int instances, String directory, int executors, long cache,
        long size, long xmx) throws ParseException {
      if (instances <= 0) {
        throw new ParseException("Invalid configuration: " + instances
            + " (should be greater than 0)");
      }
      this.instances = instances;
      this.directory = directory;
      this.name = name;
      this.executors = executors;
      this.cache = cache;
      this.size = size;
      this.xmx = xmx;
    }

    public String getName() {
      return name;
    }

    public int getInstances() {
      return instances;
    }

    public String getDirectory() {
      return directory;
    }

    public int getExecutors() {
      return executors;
    }

    public long getCache() {
      return cache;
    }

    public long getSize() {
      return size;
    }

    public long getXmx() {
      return xmx;
    }
  }

  protected static final Log l4j = LogFactory.getLog(LlapOptionsProcessor.class.getName());
  private final Options options = new Options();
  Map<String, String> hiveVariables = new HashMap<String, String>();
  private org.apache.commons.cli.CommandLine commandLine;

  @SuppressWarnings("static-access")
  public LlapOptionsProcessor() {

    // set the number of instances on which llap should run
    options.addOption(OptionBuilder.hasArg().withArgName("instances").withLongOpt("instances")
        .withDescription("Specify the number of instances to run this on").create('i'));

    options.addOption(OptionBuilder.hasArg().withArgName("name").withLongOpt("name")
        .withDescription("Cluster name for YARN registry").create('n'));

    options.addOption(OptionBuilder.hasArg().withArgName("directory").withLongOpt("directory")
        .withDescription("Temp directory for jars etc.").create('d'));

    options.addOption(OptionBuilder.hasArg().withArgName("args").withLongOpt("args")
        .withDescription("java arguments to the llap instance").create('a'));

    options.addOption(OptionBuilder.hasArg().withArgName("loglevel").withLongOpt("loglevel")
        .withDescription("log levels for the llap instance").create('l'));

    options.addOption(OptionBuilder.hasArg().withArgName("chaosmonkey").withLongOpt("chaosmonkey")
        .withDescription("chaosmonkey interval").create('m'));

    options.addOption(OptionBuilder.hasArg().withArgName("executors").withLongOpt("executors")
        .withDescription("executor per instance").create('e'));

    options.addOption(OptionBuilder.hasArg().withArgName("cache").withLongOpt("cache")
        .withDescription("cache size per instance").create('c'));

    options.addOption(OptionBuilder.hasArg().withArgName("size").withLongOpt("size")
        .withDescription("container size per instance").create('s'));

    options.addOption(OptionBuilder.hasArg().withArgName("xmx").withLongOpt("xmx")
        .withDescription("working memory size").create('w'));

    // [-H|--help]
    options.addOption(new Option("H", "help", false, "Print help information"));
  }

  private static long parseSuffixed(String value) {
    return StringUtils.TraditionalBinaryPrefix.string2long(value);
  }

  public LlapOptions processOptions(String argv[]) throws ParseException {
    commandLine = new GnuParser().parse(options, argv);
    if (commandLine.hasOption('H') || false == commandLine.hasOption("instances")) {
      // needs at least --instances
      printUsage();
      return null;
    }

    int instances = Integer.parseInt(commandLine.getOptionValue("instances"));
    String directory = commandLine.getOptionValue("directory");

    String name = commandLine.getOptionValue("name", null);

    int executors = Integer.parseInt(commandLine.getOptionValue("executors", "-1"));
    long cache = parseSuffixed(commandLine.getOptionValue("cache", "-1"));
    long size = parseSuffixed(commandLine.getOptionValue("size", "-1"));
    long xmx = parseSuffixed(commandLine.getOptionValue("xmx", "-1"));

    // loglevel, chaosmonkey & args are parsed by the python processor

    return new LlapOptions(name, instances, directory, executors, cache, size, xmx);
  }

  private void printUsage() {
    new HelpFormatter().printHelp("llap", options);
  }
}