(defproject rays "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [criterium "0.4.1"]
                 [net.mikera/core.matrix "0.9.0"]
                 [net.mikera/vectorz-clj "0.13.2"]]
  :jvm-opts ["-Xmx1g"]
  :main rays.core)
