<?php

namespace App;

ini_set("display_errors", 1);
ini_set("display_startup_errors", 1);
error_reporting(E_ALL);

//chdir("/Users/elshad/Desktop/java_obfuscator/YoutubeDownloader-master/app/src/main");
chdir("/var/www/bms.local/site_v1.0/java_obfuscator/java_obfuscator/YoutubeDownloader-master/app/src/main");

class Obfuscator {

    private $manifest = "";
    private $java = "";
    private $classes = [];

    public function __construct($manifest, $java) {
        $this->manifest = $manifest;
        $this->java = $java;
    }

    public function mkdir($dir = "./", $copyDir = "../../../../copy/") {
        // folderlerin kopiyasini yaradirig

        if (!is_dir($copyDir)) {
            mkdir($copyDir, 0777);
        }

        $arr = scandir($dir);

        foreach ($arr as $key => $val) {
            if ($val == "." || $val == "..") {
                continue;
            }

            if (is_dir($dir . $val)) {
                if (!is_dir($copyDir . $val))
                    mkdir($copyDir . $val, 0777);
                $this->mkdir($dir . $val . "/", $copyDir . $val . "/");
            }
        }
    }

    public function clearMultilineComments($content) {
        $start = strpos($content, "/*");
        $end = strpos($content, "*/");
        if ($end > 0) {
            $content1 = substr($content, 0, $start);
            $content2 = substr($content, $end + 2);
            $content = $content1 . $content2;
        }

        if (strstr($content, "/*")) {
            $content = $this->clearMultilineComments($content);
        }

        return $content;
    }

    private function recursiveScanner($path) {
        $scanned = scandir($path);

        foreach ($scanned as $key => $val) {
            if ($val == "." || $val == "..")
                continue;

            if (is_dir($path . "/" . $val)) {
                $this->recursiveScanner($path . "/" . $val);
            }

            if (is_file($path . "/" . $val) && preg_match("/\.java$/", $val)) {
                $this->classes[] = array(
                    'class' => $val,
                    'path' => $path,
                    'class_detailes' => $this->getClassDetailes($path . "/" . $val, $val)
                );
            }
        }
    }

    // get package name
    private function getPackage(&$details, $line, $className, $classFile) {
        if (preg_match("/^package/", $line)) {
            preg_match("/^package ([^;]+);$/", $line, $out);
            $details['package'] = $out[1];
        }
    }

    // get type
    private function getType(&$details, $line, $className, $classFile) {
        if (preg_match("/^(public|private|protected)?[\s]?class/", $line)) {
            $details['type'] = 'class';
        } else if (preg_match("/^(public|private|protected)?[\s]?interface/", $line)) {
            $details['type'] = 'interface';
        } else if (preg_match("/^(public|private|protected)?[\s]?abstract/", $line)) {
            $details['type'] = 'abstract';
        }
    }

    // get properties
    private function getProperties(&$details, $line, $className, $classFile) {
        static $inClassName = null;
        if (preg_match("/^[(private|public|protected)|static|final]* [^;]+/", $line)) {
            if (strstr($line, "(") || strstr($line, ")")) {
                return false;
            }

            // beraberliyin sol sag terefindeki spaceleri siliriy
            $line = preg_replace("/[\s\t\n\r]+=[\s\t\n\r]+/", "=", $line);
            // beraberliyin sol terefindeki spaceleri siliriy
            $line = preg_replace("/[\s\t\n\r]+=/", "=", $line);
            // beraberliyin sag terefindeki spaceleri siliriy
            $line = preg_replace("/=[\s\t\n\r]+/", "=", $line);
            // parcaliyirig
            $splitted = preg_split("/[\s\t;,]+/", $line);

            if ($inClassName != $classFile . "/" . $className) {
                ObfuscatedNames::$lastPropertyName = [];
            }

            $details['properties'][] = new Properties($splitted);
        }

        $inClassName = $classFile . "/" . $className;
    }

    // get properties
    private function getMethods(&$details, $line, $className, $classFile) {
        static $inClassName = null;
        if (preg_match("/^[(private|public|protected)|static|final]* [^;]+/", $line)) {
            if (!strstr($line, "(") || !strstr($line, ")") || preg_match("/[^\(\)]+=[^\(\)]+/", $line)) {
                return false;
            }

            if (preg_match("/^(if|else|for|foreach|while|switch)/", trim($line))) {
                return false;
            }

            $splitted = preg_split("/[\s\t;,]+/", $line);

            if ($inClassName != $classFile . "/" . $className) {
                ObfuscatedNames::$lastMethodName = [];
            }

            $method = new Methods($splitted);

            // on ile baslayan metodlari ignore edirik
            if (preg_match("/^on/", $method->methodName)) {
                unset($method);
                return false;
            }

            $details['methods'][] = $method;
        }

        $inClassName = $classFile . "/" . $className;
    }

    // compiler
    private function compile($line, &$details, $classFile, $className) {
        static $scope = 0;
        static $previousClass = null;

        if ($previousClass != $classFile) {
            $scope = 0;
        }

        // package
        if ($scope == 0) {
            $this->getPackage($details, $line, $className, $classFile);
        }

        // type
        if ($scope == 0) {
            $this->getType($details, $line, $className, $classFile);
        }

        // collecting properties
        if ($scope == 1 && !preg_match("/\{$/", $line)) {
            $this->getProperties($details, $line, $className, $classFile);
        }

        // collecting methods
        if ($scope == 0 && preg_match("/\{$/", $line)) {
            $this->getMethods($details, $line, $className, $classFile);
        }

        if (preg_match("/\{/", $line)) {
            $scope++;
        }
        if (preg_match("/\}/", $line)) {
            $scope--;
        }

        $previousClass = $classFile;
    }

    private function getClassDetailes($classFile, $className) {
        $className = str_replace(".java", "", $className);
        $fp = fopen($classFile, "r");

        $details = array(
            'class_name' => $className,
            'class_name_obfuscated' => ObfuscatedNames::generateClassName(),
            'package' => null,
            'type' => null,
            'properties' => [],
            'methods' => [],
            'is_activity' => false,
        );

        $content = file_get_contents($classFile);
        $content = preg_replace("/\@[^\n\r]+/", "", $content); // @override lari siliriy
        $content = preg_replace("/[\n\r]+/", "", $content); // break-leri yigisdiririg
        $content = $this->clearMultilineComments($content); // multiline kommentleri siliriy
        $content = preg_replace("/([\n\r\t\s])+/", "\\1", $content); // uzun uzadi breakleri tablari spaceleri siliriy
        $content = preg_replace("/\}/", "}\n", $content);
        $content = preg_replace("/\{/", "{\n", $content);
        $content = preg_replace("/\;/", ";\n", $content);
        $details['content'] = $content;

        $splittedContent = preg_split("/[\n\r]/", $content);

        // tek setirli kommentleri temizleyirik
        foreach ($splittedContent as $key => $line) {
            if (preg_match("/^\/{2}/", trim($line))) {
                unset($splittedContent[$key]);
                continue;
            }

            $this->compile(trim($line), $details, $classFile, $className);
        }

        return $details;
    }

    public function obfuscateInYourself() {
        foreach ($this->classes as $cKey => $row) {
            // birinci ozunde deyisiriy
            $content = $row['class_detailes']['content'];

            // klass adlarini deyisiriy
            $content = preg_replace("/class {$row['class_detailes']['class_name']}/", "class {$row['class_detailes']['class_name_obfuscated']}", $content);
            $content = preg_replace("/public[\s\t]+{$row['class_detailes']['class_name']}[\s\t]*(\<|\()/", "public {$row['class_detailes']['class_name_obfuscated']}\\1", $content);
            $content = preg_replace("/new {$row['class_detailes']['class_name']}/", "new {$row['class_detailes']['class_name_obfuscated']}", $content);
            $content = preg_replace("/{$row['class_detailes']['class_name']} /", "{$row['class_detailes']['class_name_obfuscated']} ", $content);


            // metodlari deyisiriy
            foreach ($row['class_detailes']['methods'] as $key => $method) {
                $content = preg_replace("/{$method->scope}[\s]+" . ($method->isStatic ? "static[\s]*" : "") . ($method->isFinal ? "final[\s]*" : "") . "{$method->type}[\s]+{$method->methodName}([^\(]*)\(/", "{$method->scope} " . ($method->isStatic ? "static " : "") . ($method->isFinal ? "final " : "") . "{$method->type} {$method->methodName_obfuscated}\\1(", $content);
                $content = preg_replace("/{$row['class_detailes']['class_name_obfuscated']}\.{$method->methodName}[\s]*(\<\()/", "{$row['class_detailes']['class_name_obfuscated']}\.{$method->methodName_obfuscated}\\1", $content);
            }

            // propertileri deyisiriy
            foreach ($row['class_detailes']['properties'] as $key => $property) {
                $pattern = "{$property->scope}[\s]+"
                        . ($property->isStatic ? "static[\s]+" : "")
                        . ($property->isFinal ? "final[\s]+" : "")
                        . "{$property->type}[\s]+";

                $replace = "{$property->scope} "
                        . ($property->isStatic ? "static " : "")
                        . ($property->isFinal ? "final " : "")
                        . "{$property->type} ";
                foreach ($property->name as $pKey => $name) {
                    $pattern .= "([\n\r\s\t=\(\)]+){$name}[\s]*" . ($property->value[$pKey] ? "=[^,;]+" : "") . ",[\s]*";
                    $replace .= "\\1{$property->name_obfuscated[$pKey]}" . ($property->value[$pKey] ? "={$property->value[$pKey]}" : "") . ",";
                }

                $pattern = "/" . preg_replace("/,\[\\\s\]\*$/", "", $pattern) . "/";
                $replace = preg_replace("/,$/", "", $replace);

                $content = preg_replace($pattern, $replace, $content);

                foreach ($property->name as $pKey => $name) {
                    $content = preg_replace("/([\n\r\s\t=\(\)]+)({$row['class_detailes']['class_name_obfuscated']}\.)?{$name}[\s]*/", "\\1\\2{$property->name_obfuscated[$pKey]}", $content);
                }
            }

            $this->classes[$cKey]['class_detailes']['content_obfuscated'] = $content;
        }
    }

    function obfuscateInOthers() {
        $objects = [];
        foreach ($this->classes as $cKey => $row) {
            $class1 = $row['class_detailes'];
            foreach ($this->classes as $eKey => $eRow) {
                $class2 = $eRow['class_detailes'];

                // oz faylini ignore edirik
                if ($cKey == $eKey) {
                    continue;
                }
                $content = $eRow['class_detailes']['content_obfuscated'];
                $splittedContent = preg_split("/[\n\r]/", $content);

                $scope = 0;

                foreach ($splittedContent as $key => $line) {
                    $line = trim($line);

                    // package elaqelerini yoxlayiriq
                    $relation = false;
                    if ($class1['package'] != $class2['package']) {
                        if ($scope == 0) {
                            if (preg_match("/^import[\s\t]+([^;]+);/", $line, $out)) {
                                if ($out[1] == $class2['package']) {
                                    $relation = true;
                                }
                            }
                        }
                    } else {
                        $relation = true;
                    }

                    if (preg_match("/\{/", $line)) {
                        $scope++;
                    }
                    if (preg_match("/\}/", $line)) {
                        $scope--;
                    }
                }

                // package elaqesi olmadigina gore cari klasi ignore edirik
                if (!$relation) {
                    continue;
                }

                $scope = 0;

                $newContent = [];

                foreach ($splittedContent as $key => $line) {
                    $line = trim($line);

                    if (preg_match("/\{/", $line)) {
                        $scope++;
                    }
                    if (preg_match("/\}/", $line)) {
                        $scope--;
                    }

                    // implement ve extendleri replace eliyiriy
                    if ($scope == 1) {
                        $line = preg_replace("/(implements|extends)[\s\t]+([^\{]*)({$class1['class_name']})([^\{]*){/", "\\1 \\2{$class1['class_name_obfuscated']}\\4{", $line);
                    }

                    // className.class ve className.this replace edirik
                    $line = preg_replace("/{$class1['class_name']}\.(class|this)/", "{$class1['class_name_obfuscated']}.\\1", $line);

                    // mecburi castlari replace edirik
                    $line = preg_replace("/\([\s\t]*{$class1['class_name']}[\s\t]*\)/", "({$class2['class_name_obfuscated']})", $line);

                    // new ClassName-leri evezleyiriy
                    $line = preg_replace("/new {$class1['class_name']}[\s\t]*([^\(]*)\(/", "new {$class1['class_name_obfuscated']}\\1(", $line);

                    // obyektleri toplayiriq
                    if (preg_match("/{$class1['class_name']}[\s\t]+([^;]+);/", $line, $out)) {
                        $line = preg_replace("/{$class1['class_name']}[\s\t]+/", "{$class1['class_name_obfuscated']} ", $line);
                        $vars = preg_split("/,/", $out[1]);
                        if (!empty($vars)) {
                            foreach ($vars as $key => $val) {
                                $val = preg_split("/[\s\t]*=[\s\t]*/", $val);
                                $objects[] = array(
                                    'object' => trim($val[0]),
                                    'scope' => $scope,
                                    'class' => $class1
                                );
                            }
                        }
                    }

                    $newContent[$key] = $line;
                }

                foreach ($splittedContent as $key => $line) {
                    $line = trim($line);

                    if (preg_match("/\{/", $line)) {
                        $scope++;
                    }
                    if (preg_match("/\}/", $line)) {
                        $scope--;
                    }

                    foreach ($objects as $oKey => $row) {
                        if ($scope >= $row['scope']) {
                            foreach ($row['class']['properties'] as $pKey => $property) {
                                $line = preg_replace("/{$row['object']}\.{$property->name}([^\(]+)/", "{$row['object']}.{$property->name_obfuscated}\\1", $line);
                            }

                            foreach ($row['class']['methods'] as $pKey => $method) {
                                $line = preg_replace("/{$row['object']}\.{$method->methodName}([^\(]*)\(/", "{$row['object']}.{$method->methodName_obfuscated}\\1(", $line);
                            }
                        }
                    }
                }

                $this->classes[$eKey]['class_detailes']['content_obfuscated'] = implode("\n", $newContent);
            }
        }
    }

    public function obfuscateInOthers_bkp() {
        foreach ($this->classes as $cKey => $row) {
            foreach ($this->classes as $eKey => $eRow) {
                // oz faylini ignore edirik
                if ($cKey == $eKey) {
                    continue;
                }
                $content = $eRow['class_detailes']['content_obfuscated'];

                // klaslari deyisirik
                $content = preg_replace("/new {$row['class_detailes']['class_name']}/", "new {$row['class_detailes']['class_name_obfuscated']}", $content);
                $content = preg_replace("/{$row['class_detailes']['class_name']}/", "{$row['class_detailes']['class_name_obfuscated']}", $content);



                // metodlari deyisiriy
                foreach ($row['class_detailes']['methods'] as $key => $method) {
                    if ($method->scope != 'public') {
                        continue;
                    }

                    $content = preg_replace("/\.{$method->methodName}([^\(]*)\(/", ".{$method->type} {$method->methodName_obfuscated}\\1(", $content);
                }

                // propertileri deyisiriy
                foreach ($row['class_detailes']['properties'] as $key => $property) {
                    $pattern = "";
                    $replace = "";
                    foreach ($property->name as $pKey => $name) {
                        $pattern .= "{$name}[\s]*" . ($property->value[$pKey] ? "=[^,;]+" : "") . ",[\s]*";
                        $replace .= "{$property->name_obfuscated[$pKey]}" . ($property->value[$pKey] ? "={$property->value[$pKey]}" : "") . ",";
                    }

                    $pattern = "/" . preg_replace("/,\[\\\s\]\*$/", "", $pattern) . "/";
                    $replace = preg_replace("/,$/", "", $replace);

                    //echo $pattern . "<br>" . $replace . "<br><hr><br>";

                    $content = preg_replace($pattern, $replace, $content);

                    foreach ($property->name as $pKey => $name) {
                        $content = preg_replace("/({$row['class_detailes']['class_name_obfuscated']}\.)?{$name}[\s]*/", "\\1{$property->name_obfuscated[$pKey]}", $content);
                    }
                }

                $this->classes[$eKey]['class_detailes']['content_obfuscated'] = $content;
            }
        }
    }

    public function startObfuscate() {
        $this->recursiveScanner($this->java);

        $this->obfuscateInYourself();
        $this->obfuscateInOthers();

        echo "<pre>";
        print_r($this->classes);
    }

    // save new files
    public function saveFiles() {
        chdir("../../../../copy");
        foreach ($this->classes as $key => $row) {
            $fp = fopen($row['path'] . "/" . $row['class_detailes']['class_name_obfuscated'] . ".java", "w");
            fwrite($fp, $row['class_detailes']['content_obfuscated']);
            fclose($fp);
        }
    }

}

$manifest = "./AndroidManifest.xml";
$java = "./java";

$obfuscator = new Obfuscator($manifest, $java);

$obfuscator->mkdir();

$obfuscator->startObfuscate();

class Methods {

    public $type;
    public $scope;
    public $isStatic;
    public $isFinal;
    public $methodName;
    public $methodName_obfuscated;
    public $localVariables = [];

    public function __construct($params = array()) {
        $strange = false;

        foreach ($params as $key => $val) {
            if (!trim($val))
                continue;

            if (preg_match("/^(private|public|protected)$/i", trim($val))) {
                $this->scope = strtolower(trim($val));
            } else if (preg_match("/^static$/i", trim($val))) {
                $this->isStatic = true;
            } else if (preg_match("/^final$/i", trim($val))) {
                $this->isFinal = true;
            } else if (!$strange) {
                $this->type = trim($val);
                $strange = true;
            } else {
                $splitted = preg_split("/[\s\(]/", trim($val));
                $this->methodName = $splitted[0];
                $this->methodName_obfuscated = ObfuscatedNames::generateMethodName();
                break;
            }
        }

        unset($strange);
    }

}

class Properties {

    public $name = [];
    public $value = [];
    public $name_obfuscated = [];
    public $type;
    public $scope;
    public $isStatic;
    public $isFinal;

    public function __construct($params = array()) {
        $strange = false;

        foreach ($params as $key => $val) {
            if (!trim($val))
                continue;

            if (preg_match("/^(private|public|protected)$/i", trim($val))) {
                $this->scope = strtolower(trim($val));
            } else if (preg_match("/^static$/i", trim($val))) {
                $this->isStatic = true;
            } else if (preg_match("/^final$/i", trim($val))) {
                $this->isFinal = true;
            } else if (!$strange) {
                $this->type = trim($val);
                $strange = true;
            } else {
                $splitted = preg_split("/=/", trim($val));
                $this->name[] = $splitted[0];
                $this->value[] = isset($splitted[1]) ? $splitted[1] : null;
                $this->name_obfuscated[] = ObfuscatedNames::generatePropertyName();
            }
        }

        unset($strange);
    }

}

class Variables {

    public $name;
    public $type;

}

class ObfuscatedNames {

    public static $lastClassName = [];
    public static $lastPropertyName = [];
    public static $lastMethodName = [];
    private static $alphabet = array(
        'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z'
    );

    public static function generateClassName() {
        if (self::$lastClassName == null) {
            self::$lastClassName[0] = self::$alphabet[0];
            return implode("", self::$lastClassName);
        } else {
            $length = strlen(implode("", self::$lastClassName));
            if ($length == 1) {
                $key = array_search(self::$lastClassName[0], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastClassName[0] = self::$alphabet[$key + 1];
                    return implode("", self::$lastClassName);
                } else {
                    self::$lastClassName[0] = self::$alphabet[0];
                    self::$lastClassName[1] = self::$alphabet[0];
                    return implode("", self::$lastClassName);
                }
            } else if ($length == 2) {
                $key = array_search(self::$lastClassName[1], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastClassName[1] = self::$alphabet[$key + 1];
                    return implode("", self::$lastClassName);
                } else {
                    $key = array_search(self::$lastClassName[0], self::$alphabet);
                    if (isset(self::$alphabet[$key + 1])) {
                        self::$lastClassName[0] = self::$alphabet[$key + 1];
                        self::$lastClassName[1] = self::$alphabet[0];
                        return implode("", self::$lastClassName);
                    } else {
                        self::$lastClassName[0] = self::$alphabet[0];
                        self::$lastClassName[1] = self::$alphabet[0];
                        self::$lastClassName[2] = self::$alphabet[0];
                        return implode("", self::$lastClassName);
                    }
                }
            } else if ($length == 3) {
                $key = array_search(self::$lastClassName[2], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastClassName[2] = self::$alphabet[$key + 1];
                    return implode("", self::$lastClassName);
                } else {
                    $key = array_search(self::$lastClassName[1], self::$alphabet);
                    if (isset(self::$alphabet[$key + 1])) {
                        self::$lastClassName[1] = self::$alphabet[$key + 1];
                        self::$lastClassName[2] = self::$alphabet[0];
                        return implode("", self::$lastClassName);
                    }
                }
            }
        }
    }

    public static function generatePropertyName() {
        if (self::$lastPropertyName == null) {
            self::$lastPropertyName[0] = self::$alphabet[0];
            return implode("", self::$lastPropertyName);
        } else {
            $length = strlen(implode("", self::$lastPropertyName));
            if ($length == 1) {
                $key = array_search(self::$lastPropertyName[0], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastPropertyName[0] = self::$alphabet[$key + 1];
                    return implode("", self::$lastPropertyName);
                } else {
                    self::$lastPropertyName[0] = self::$alphabet[0];
                    self::$lastPropertyName[1] = self::$alphabet[0];
                    return implode("", self::$lastPropertyName);
                }
            } else if ($length == 2) {
                $key = array_search(self::$lastPropertyName[1], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastPropertyName[1] = self::$alphabet[$key + 1];
                    return implode("", self::$lastPropertyName);
                } else {
                    $key = array_search(self::$lastPropertyName[0], self::$alphabet);
                    if (isset(self::$alphabet[$key + 1])) {
                        self::$lastPropertyName[0] = self::$alphabet[$key + 1];
                        self::$lastPropertyName[1] = self::$alphabet[0];
                        return implode("", self::$lastPropertyName);
                    } else {
                        self::$lastPropertyName[0] = self::$alphabet[0];
                        self::$lastPropertyName[1] = self::$alphabet[0];
                        self::$lastPropertyName[2] = self::$alphabet[0];
                        return implode("", self::$lastPropertyName);
                    }
                }
            } else if ($length == 3) {
                $key = array_search(self::$lastPropertyName[2], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastPropertyName[2] = self::$alphabet[$key + 1];
                    return implode("", self::$lastPropertyName);
                } else {
                    $key = array_search(self::$lastPropertyName[1], self::$alphabet);
                    if (isset(self::$alphabet[$key + 1])) {
                        self::$lastPropertyName[1] = self::$alphabet[$key + 1];
                        self::$lastPropertyName[2] = self::$alphabet[0];
                        return implode("", self::$lastPropertyName);
                    }
                }
            }
        }
    }

    public static function generateMethodName() {
        if (self::$lastMethodName == null) {
            self::$lastMethodName[0] = self::$alphabet[0];
            return implode("", self::$lastMethodName);
        } else {
            $length = strlen(implode("", self::$lastMethodName));
            if ($length == 1) {
                $key = array_search(self::$lastMethodName[0], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastMethodName[0] = self::$alphabet[$key + 1];
                    return implode("", self::$lastMethodName);
                } else {
                    self::$lastMethodName[0] = self::$alphabet[0];
                    self::$lastMethodName[1] = self::$alphabet[0];
                    return implode("", self::$lastMethodName);
                }
            } else if ($length == 2) {
                $key = array_search(self::$lastMethodName[1], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastMethodName[1] = self::$alphabet[$key + 1];
                    return implode("", self::$lastMethodName);
                } else {
                    $key = array_search(self::$lastMethodName[0], self::$alphabet);
                    if (isset(self::$alphabet[$key + 1])) {
                        self::$lastMethodName[0] = self::$alphabet[$key + 1];
                        self::$lastMethodName[1] = self::$alphabet[0];
                        return implode("", self::$lastMethodName);
                    } else {
                        self::$lastMethodName[0] = self::$alphabet[0];
                        self::$lastMethodName[1] = self::$alphabet[0];
                        self::$lastMethodName[2] = self::$alphabet[0];
                        return implode("", self::$lastMethodName);
                    }
                }
            } else if ($length == 3) {
                $key = array_search(self::$lastMethodName[2], self::$alphabet);
                if (isset(self::$alphabet[$key + 1])) {
                    self::$lastMethodName[2] = self::$alphabet[$key + 1];
                    return implode("", self::$lastMethodName);
                } else {
                    $key = array_search(self::$lastMethodName[1], self::$alphabet);
                    if (isset(self::$alphabet[$key + 1])) {
                        self::$lastMethodName[1] = self::$alphabet[$key + 1];
                        self::$lastMethodName[2] = self::$alphabet[0];
                        return implode("", self::$lastMethodName);
                    }
                }
            }
        }
    }

}
