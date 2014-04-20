# ball-clock

A bit of code to simulate a ball-clock, and analyze how many days a clock with n balls can continue with a unique cofiguration of [distinct] balls, before returning to its original configuration.

## Usage

```clj
; Create a ball-clock
(ball-clock 4)
; => {:ones [] :fives [] :hours [] :queue <-(0 1 2 3)-<}

; How many minutes can the ball clock represent?
(maximum-minutes (ball-clock 5))
; => 9

; Increment the "minutes" field in a clock
(increment-minutes (ball-clock 3))
; => {:ones [0] :fives [] :hours [] :queue <-(1 2)-<}

; Increment the minutes a given number of times
(increment-minutes (ball-clock 5) 6)
; => {:ones [3] :fives [4] :hours [] :queue <-(2 1 0)-<}

; Analyze how many unique 12-hour cycles a clock with the given number of balls will have
(unique-cycles 29)
; => 204
```

## License

Copyright © 2014 Paul Allen Richardson

    Distributed under the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
    By using this software in any fashion, you are
    agreeing to be bound by the terms of this license. You must
    not remove this notice, or any other, from this software.
