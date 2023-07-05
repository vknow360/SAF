# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.sunny.saf.SAF {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/sunny/saf/repack'
-flattenpackagehierarchy
-dontpreverify
