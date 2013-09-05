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

(defprotocol Shape
  "Object that can be rendered inside the scene"
  (intersect [this ray] "returns either null or the distance from the ray to the intersection of the object"))
(defrecord Sphere [loc r material]
  Shape
  (intersect [this ray]
    ;; See: http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-7-intersecting-simple-shapes/ray-sphere-intersection/
    (let [l (- (:loc ray) (:loc this))
        a 1 ;; If (:dir ray) isn't normalised this will be wrong
        b (* 2 (dot (:dir ray) l))
        c (- (dot l l) (* (:r this) (:r this)))]
    (-> (quadratic a b c) sort first))))


(defn view-ray
  "Currently only resolution dependent Perpendicular / Orthographic projection,
  may change one day"
  [x y camera]
  (assoc camera :loc (+ (:loc camera) [x y 0])))

(defn relevant-object [ray objects]
  (first (reduce
    (fn [x object] (let [oi (intersect object ray)]
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
    (Sphere.
      (matrix [-150 0 0])
      100
      {:type :color
       :color (rand-color)})
    (Sphere.
      (matrix [150 0 0])
      100
      {:type :color
       :color (rand-color)})
    (Sphere.
      (matrix [0 25 -50])
      100
      {:type :color
       :color (rand-color)})
    (Sphere.
      (matrix [-150 -175 0])
      25
      {:type :color
       :color (rand-color)})
    (Sphere.
      (matrix [150 -125 0])
      75
      {:type :color
       :color (rand-color)})
    (Sphere.
      (matrix [0 -125 -50])
      50
      {:type :color
       :color (rand-color)})
    ]
    [{:type :point
      :loc (matrix [0 200 -100])}]))
