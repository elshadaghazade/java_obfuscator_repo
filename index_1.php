<?php

namespace App;

chdir("/home/elshad/Desktop/YoutubeDownloader-master/app/src/main");

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

    // package tapir
    private function setPackage($line, &$details) {
        if (preg_match("/^package/", $line)) {
            $details['package'] = preg_replace("/package[\s\t\r\n]+(.+);[\s\t\r\n]*/i", "\\1", $line);
        }
    }

    // package tapir
    private function setType($line, $className, &$details) {
        if (preg_match("/^(public|private|protected|static)[\s\t\r\n]+(interface|class|abstract)[\s\t\r\n]+{$className}/i", $line, $out)) {
            $details['type'] = $out[2];

            // setting activity flag
            if (preg_match("/Activity$/", $className)) {
                $details['is_activity'] = true;
            }
        }
    }

    // set extends
    private function setExtends($line, $className, &$details) {
        if (preg_match("/{$className}[\s\t\r\n]+extends[\s\t\r\n]+([^\s\t\r\n]+)/", $line, $out)) {
            $details['extends'] = $out[1];
        }
    }

    // set implements
    private function setImplements($content, &$details) {
        if (preg_match("/{$className}.+implements[\s\t\r\n]+([^\{]+)/i", $content, $out)) {
            $details['implements'] = preg_split("/[\s\t\r\n]*,[\s\t\r\n]*/", trim($out[1], " {"));
        }
    }

    // set properties
    private function setProperties($line, &$details) {
        static $enteredIntoClass = false;
        static $lineStarted = false;
        
        if (preg_match("/^(public|private|protected|static)[\s\t\r\n]+(interface|class|abstract)[\s\t\r\n]+{$className}/i", $line, $out)) {
            $enteredIntoClass = true;
        }
        
        
        
        if ($enteredIntoClass && preg_match("/(private|public|protected|static|final)([^;]+)/", trim($line), $out)) {
            echo "<pre>";
            print_r($out);
        }
    }

    private function getClassDetailes($classFile, $className) {
        $className = str_replace(".java", "", $className);
        $fp = fopen($classFile, "r");

        $content = "";

        $details = array(
            'package' => null,
            'type' => null,
            'extends' => null,
            'implements' => [],
            'properties' => [],
            'methods' => [],
            'is_activity' => false,
        );

        if ($fp) {
            while (($line = fgets($fp)) !== false) {
                $content .= $line;
                // package tapiriq
                $this->setPackage($line, $details);

                // type tapiriq
                $this->setType($line, $className, $details);

                // set extends
                $this->setExtends($line, $className, $details);

                // set properties
                $this->setProperties($line, $details);
            }

            fclose($handle);
        } else {
            throw new Exception("Fayli achilmadi");
        }
        
        $details['content'] = $content;

        // set implements
        $this->setImplements($content, $details);

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

    public $name;
    public $type;
    public $scope;
    public $isStatic;

}

class Variables {

    public $name;
    public $type;

}
