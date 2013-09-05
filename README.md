# rays

Me playing around with writing a simple ray tracer in Clojure, so I can work through
making code all fast and stuff.

## Performance notes

On my 2009 MBP:

    rays.core=> (require '[criterium.core :as c])
    nil
    rays.core=> (c/bench (count (test-draw [1280 720])))
    WARNING: Final GC required 5.347861768938507 % of runtime
    Evaluation count : 60 in 60 samples of 1 calls.
                 Execution time mean : 2.022414 sec
        Execution time std-deviation : 35.133898 ms
       Execution time lower quantile : 1.980376 sec ( 2.5%)
       Execution time upper quantile : 2.081969 sec (97.5%)
                       Overhead used : 3.769332 ns

    Found 1 outliers in 60 samples (1.6667 %)
      low-severe   1 (1.6667 %)
     Variance from outliers : 6.2832 % Variance is slightly inflated by outliers
    rays.core=> (def picture-data (test-draw [1280 720]))
    #'rays.core/picture-data
    rays.core=> (count picture-data)
    720
    rays.core=> (c/bench (buffered-image picture-data))
    Evaluation count : 60 in 60 samples of 1 calls.
                 Execution time mean : 1.058711 sec
        Execution time std-deviation : 6.976462 ms
       Execution time lower quantile : 1.052282 sec ( 2.5%)
       Execution time upper quantile : 1.073940 sec (97.5%)
                       Overhead used : 3.769236 ns

    Found 12 outliers in 60 samples (20.0000 %)
      low-severe   10 (16.6667 %)
      low-mild   2 (3.3333 %)
     Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

## License

Copyright © 2013 SCdF

Distributed under the Eclipse Public License, the same as Clojure.
