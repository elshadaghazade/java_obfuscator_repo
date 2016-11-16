<?php

namespace App;

ini_set("display_errors", 1);
ini_set("display_startup_errors", 1);
error_reporting(E_ALL);

chdir("/Users/elshad/Desktop/java_obfuscator/YoutubeDownloader-master/app/src/main");

class Obfuscator {

    private $manifest = "";
    private $java = "";
    private $classes = [];

    public function __construct($manifest, $java) {
        $this->manifest = $manifest;
        $this->java = $java;
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

    // compiler
    private function combine($content, &$details, $classFile) {
        
        // clean comments
        $content = preg_replace("/\/\*[\w\W]*\*\//", "", $content);
        $content = preg_replace("/\/{2}[^\n\r]*/", "", $content);
        
        $splitted = preg_split("/[;\{\}]/", $content);
        
        echo "<pre>";
        print_r($splitted);
    }

    private function getClassDetailes($classFile, $className) {
        $className = str_replace(".java", "", $className);
        $details = array(
            'classes' => [],
            'interfaces' => [],
            'abstracts' => [],
            'methods' => [],
            'properties' => [],
        );

        $content = file_get_contents($classFile);

        $this->combine($content, $details, $classFile);

        return $details;
    }

    public function startObfuscate() {
        $this->recursiveScanner($this->java);

        echo "<pre>";
        print_r($this->classes);
    }

}

$manifest = "./AndroidManifest.xml";
$java = "./java";

$obfuscator = new Obfuscator($manifest, $java);

$obfuscator->startObfuscate();

class Methods {

    public $methodName;
    public $localVariables = [];

}

class Properties {
    
    public function __construct($params = array()) {
        echo "<pre>";
        print_r($params);
    }

    public $name;
    public $type;
    public $scope;
    public $isStatic;

}

class Variables {

    public $name;
    public $type;

}
