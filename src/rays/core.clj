(ns rays.core
  (:use clojure.core.matrix)
  (:use clojure.core.matrix.operators)
  (:refer-clojure :exclude [* - + == /])
  (:import java.awt.image.BufferedImage
           java.awt.Color
           java.io.File
           javax.imageio.ImageIO))

(set! *warn-on-reflection* true)

;;
;; Image gen
;;
(defn buffered-image
  [data]
  (let [height (count data)
        width (count (first data))
        bimage (BufferedImage. width height BufferedImage/TYPE_INT_RGB)
        adata (->> data flatten (map (fn [^Color c] (.getRGB c))) int-array)]
    (.setRGB bimage 0 0 width height adata 0 width)
    bimage))

(defn image->file [^BufferedImage image ^String filename] (ImageIO/write image "png" (File. filename)))

(defn dumpdata [data ^String filename]
  (-> data buffered-image (image->file filename)))
;;
;; Tracin' rays
;;

(defn quadratic [a b c]
  "Solves ax^2 + bx + c = 0 for x"
  (let [discr (- (* b b) (* 4 a c))]
    (cond
      (< discr 0) []
      (= discr 0) [(* -0.5 (/ b a))]
      :else (let [+- (if (> b 0) + -)
                  q  (* -0.5 (+- b (Math/sqrt discr)))]
              [(/ q a) (/ c q)]))))

(defmulti intersect
  "Returns the closest intersect distance for the given object, or nil if no intersection takes place"
  (fn [ray object] (:type object)))
(defmethod intersect :sphere [ray sphere]
  ;; See: http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-7-intersecting-simple-shapes/ray-sphere-intersection/
  ;;(println "(-" (:loc ray) (:loc sphere) ")")
  (let [l (- (:loc ray) (:loc sphere))
        a 1 ;; If (:dir ray) isn't normalised this will be wrong
        b (* 2 (dot (:dir ray) l))
        c (- (dot l l) (* (:r sphere) (:r sphere)))]
    (-> (quadratic a b c) sort first)))

(defn ray
  "Currently only resolution dependent Perpendicular / Orthographic projection,
  may change one day"
  [x y camera]
  (assoc camera :loc (+ (:loc camera) [x y 0])))

(defn <nil [a b]
  (cond
    (and a b) (< a b)
    a true
    :else false))


(defn calc-pixel ^Color [x y camera objects lights]
  (let [view-ray (ray x y camera)]
    (if-let [obj (first (sort-by (partial intersect view-ray) <nil objects))]
      (get-in obj [:material :color])
      Color/BLACK)))

(defn trace-image [[width height] camera objects lights]
  (mapv
    (fn [y] (mapv (fn [x] (calc-pixel x y camera objects lights)) (range width)))
    (range height)))

(defn test-draw [wh]
  (trace-image wh
               {:type :orthographic
                :loc [-320 -240 -1000]
                :dir [0 0 1]} ;; TODO make sure length == 1 (it is in this simple case)
               [{:type :sphere
                 :loc [-150 0 0]
                 :r 100
                 :material {:type :color
                            :color Color/RED}}
                {:type :sphere
                 :loc [150 0 0]
                 :r 100
                 :material {:type :color
                            :color Color/BLUE}}]
               [{:type :point
                 :loc [0 200 -100]}]))

;;
;; etc
;;

(defn -main
  [& args]
  (println "Nothing yet!"))

(defn- silly-random-color [i]
  (Color. ^int (- 255 (rem i 255)) ^int (rem i 255) ^int (rand-int 255)))

(defn- silly-random-image-row [width]
  (mapv silly-random-color (range width)))

(defn silly-random-image
  [width height]
  (mapv (fn [_] (silly-random-image-row width)) (range height)))
