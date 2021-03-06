yummy: YAML configuration for Clojure application
=================================================

[![Build Status](https://secure.travis-ci.org/exoscale/yummy.png)](http://travis-ci.org/exoscale/yummy)
[![Clojars Project](https://img.shields.io/clojars/v/exoscale/yummy.svg)](https://clojars.org/exoscale/yummy)

Yummy allows reading in configuration from YAML files and
provides optional facilities for:

- Environment based configuration
- Validation through `clojure.spec`

Yummy only provides YAML parsing, no generation facilities are present.

## Using

Auto determined configuration file paths:

```clojure
(spec/def ::config (spec/keys :req-un [::http ::logging ::database]))
(yummy.config/load-config {:program-name :mydaemon :spec ::config})
```

Yummy will look for a configuration-file path in the
`mydaemon.configuration` system property or the
`MYDAEMON_CONFIGURATION` environment variable.

A path can outright be given as well:

```clojure
(spec/def ::config (spec/keys :req-un [::http ::logging ::database]))
(yummy.config/load-config {:path "/etc/mydaemon.yml" :spec ::config})
```

Alternately configuration can be loaded from a string:

```clojure
(yummy.config/load-config-string "a: b" {})
```

## Additional YAML Tags

To make integration as simple as possible, Yummy understands a number of
custom tag parsers, namely:

- `envdir`: loads a map from a directory, treating file names as keys and content as values
- `envvar`: loads a value from the environment, optionally taking in defaults
- `keyword`: coerce a string to a keyword
- `envfmt`: produce a string from a format string and environment variables to pull in
- `slurp`: produce a string from the given file content
- `uuid`: produce a UUID from the given string

## Example

```yaml
a: !keyword b
b: !envvar HOME
c: !envvar [NOPE, hello]
d: !envdir /tmp/foo
e: !envfmt ["user=%s, home=%s", USER, HOME]
f: !slurp "/etc/hostname"
g: !uuid fc716a9b-fb1e-4ebd-b781-5ca13039aa55
```

## Documentation

http://exoscale.github.io/yummy

## Installation

```clojure
    [[exoscale/yummy "0.2.6"]]
```

