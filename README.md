# rays

Me playing around with writing a simple ray tracer in Clojure, so I can work through
making code all fast and stuff.

## Performance notes

On my 2009 MBP:

    rays.core=> (require '[criterium.core :as c])
    nil
    rays.core=> (c/bench (count (test-draw [1280 720])))
    WARNING: Final GC required 4.662652496528738 % of runtime
    Evaluation count : 60 in 60 samples of 1 calls.
                 Execution time mean : 2.340375 sec
        Execution time std-deviation : 26.954624 ms
       Execution time lower quantile : 2.306834 sec ( 2.5%)
       Execution time upper quantile : 2.386017 sec (97.5%)
                       Overhead used : 3.769236 ns
    nil
    rays.core=> (def picture-data (test-draw [1280 720]))
    #'rays.core/picture-data
    rays.core=> (count picture-data)
    720
    rays.core=> (c/bench (buffered-image picture-data))
    Evaluation count : 60 in 60 samples of 1 calls.
                 Execution time mean : 1.696034 sec
        Execution time std-deviation : 186.284777 ms
       Execution time lower quantile : 1.595770 sec ( 2.5%)
       Execution time upper quantile : 2.234221 sec (97.5%)
                       Overhead used : 3.769236 ns

    Found 7 outliers in 60 samples (11.6667 %)
      low-severe   2 (3.3333 %)
      low-mild   1 (1.6667 %)
      high-mild  1 (1.6667 %)
      high-severe  3 (5.0000 %)
     Variance from outliers : 73.7858 % Variance is severely inflated by outliers
    nil

## License

Copyright © 2013 SCdF

Distributed under the Eclipse Public License, the same as Clojure.
