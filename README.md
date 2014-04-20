# ball-clock

A bit of code to simulate a ball-clock, and analyze how many days a clock with n balls can continue with a unique cofiguration of [distinct] balls, before returning to its original configuration.

## Usage

```clj
; Create a ball-clock
(ball-clock n)

; Analyze how many unique cycles it will have
(unique-cycles (ball-clock 30))
; => 30
```

## License

Copyright © 2014 Paul Allen Richardson

  Distributed under the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
  By using this software in any fashion, you are
agreeing to be bound by the terms of this license. You must
not remove this notice, or any other, from this software.
