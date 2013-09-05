(ns rays.core
  (:use clojure.core.matrix)
  (:use clojure.core.matrix.operators)
  (:refer-clojure :exclude [* - + == /])
  (:import java.awt.image.BufferedImage
           java.awt.Color
           java.io.File
           javax.imageio.ImageIO))

(set! *warn-on-reflection* true)

(set-current-implementation :vectorz)

;; Crazy utils I should split off
(defmacro partialn
  [n f & args]
  (let [params (map (fn [_] (gensym "fnp-")) (range n))]
    `(fn [~@params] (~f ~@args ~@params))))

(defn color->RGB ^long [^Color c] (.getRGB c))
(defn colors->RGB [colors] (mapv color->RGB colors))
;;
;; Image gen
;;
(defn buffered-image
  [data]
  (let [height (count data)
        width (count (first data))
        bimage (BufferedImage. width height BufferedImage/TYPE_INT_RGB)
        adata (->> data flatten colors->RGB int-array)]
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
  (let [l (- (:loc ray) (:loc sphere))
        a 1 ;; If (:dir ray) isn't normalised this will be wrong
        b (* 2 (dot (:dir ray) l))
        c (- (dot l l) (* (:r sphere) (:r sphere)))]
    (-> (quadratic a b c) sort first)))


(defn view-ray
  "Currently only resolution dependent Perpendicular / Orthographic projection,
  may change one day"
  [x y camera]
  (assoc camera :loc (+ (:loc camera) [x y 0])))

(defn relevant-object [ray objects]
  (first (reduce
    (fn [x object] (let [oi (intersect ray object)]
                      (cond
                        (nil? oi) x
                        (or (nil? x) (< oi (peek x))) [object oi]
                        :else x)))
    nil
    objects)))

(defn calc-pixel ^Color [x y camera objects lights]
  (let [ray (view-ray x y camera)]
    (if-let [obj (relevant-object ray objects)]
      (get-in obj [:material :color])
      Color/BLACK)))

(defn trace-image [[width height] camera objects lights]
  (pmap
    (fn [y] (mapv (fn [x] (calc-pixel x y camera objects lights)) (range width)))
    (range height)))

(defn rand-color []
  (Color. ^int (rand-int 255) ^int (rand-int 255) ^int (rand-int 255)))

(defn test-draw [wh]
  (trace-image wh
               {:type :orthographic
                :loc (matrix [-640 -460 -200])
                :dir (normalise (matrix [0 10 50]))} ;; TODO make sure length == 1 (it is in this simple case)
               [
               {:type :sphere
                 :loc (matrix [-150 0 0])
                 :r 100
                 :material {:type :color
                            :color (rand-color)}}
                {:type :sphere
                 :loc (matrix [150 0 0])
                 :r 100
                 :material {:type :color
                            :color (rand-color)}}
                {:type :sphere
                 :loc (matrix [0 25 -50])
                 :r 100
                 :material {:type :color
                            :color (rand-color)}}
               {:type :sphere
                 :loc (matrix [-150 -175 0])
                 :r 25
                 :material {:type :color
                            :color (rand-color)}}
                {:type :sphere
                 :loc (matrix [150 -125 0])
                 :r 75
                 :material {:type :color
                            :color (rand-color)}}
                {:type :sphere
                 :loc (matrix [0 -125 -50])
                 :r 50
                 :material {:type :color
                            :color (rand-color)}}
                ]
               [{:type :point
                 :loc (matrix [0 200 -100])}]))