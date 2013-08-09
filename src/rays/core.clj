(ns rays.core
  (:import java.awt.image.BufferedImage
           java.awt.Color
           java.io.File
           javax.imageio.ImageIO))

(set! *warn-on-reflection* true)

(defn buffered-image
  [data]
  (let [height (count data)
        width (count (first data))
        bimage (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (doseq [x (range width)
            y (range height)]
      (let [^Color cval ((data y) x)]
        (.setRGB bimage x y (.getRGB cval))))
    bimage))

(defn image->file [^BufferedImage image ^String filename] (ImageIO/write image "png" (File. filename)))

(defn- silly-random-color [_]
  (Color. ^int (rand-int 255) ^int (rand-int 255) ^int (rand-int 255)))

(defn- silly-random-image-row [width]
  (vec (map silly-random-color (range width))))

(defn silly-random-image
  [width height]
  (vec (map (fn [_] (silly-random-image-row width)) (range height))))

(defn -main
  [& args]
  (println "Hello, World!"))
