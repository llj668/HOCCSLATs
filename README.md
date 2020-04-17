# Healthcare-Oriented Computerised Chinese Speech and Language Assessment Tools

This is a group research project repository of the project HOCCSLATs, by GRP team 03. The repository contains the source code of the software.

## Dependency

 * Java SE Development Kit 8
 * HanLP data (optional)
 * Chinese word2vec model (optional)

## Introduction to the project

Healthcare-oriented computerised Chinese speech and language assessment tools is a group research project of module: Software Engineering Group Project. This project seeks to produce computerized Chinese language assessment software for child healthcare. Also, the software can provide assistance for children language evaluation processes and linguistic research. In order to achieve this, the team has gone through several full cycles of software engineering processes, including background research, requirements engineering, software design, implementation and testing. As a result, a functional prototype has been developed.

[View the full report on this project](https://pan.baidu.com/s/1i6ZeRzJrWDhqH6pj4ObwwQ)
(Password: 18qn)


## Data files (optional)

These data files are the key components of the natural language processing tools. They are used in the grammar test to help the auto-analysis.

> *These files are large. If you choose not to download these files, the software can still run. However, the auto-analysis function will be disabled.*

### Install

#### HanLP data

1. Download [Data for HanLP](http://nlp.hankcs.com/download.php?file=data).

2. Extract the folder.

3. Modify the **hanlp.properties** and point the root to the folder.

#### Word2vec model

1. Download [Word2vec model](https://pan.baidu.com/s/19sqMz-JAhhxh3o6ecvQxQw).

2. Extract the **sgns.renmin.word** file.

3. Put the file in *src* folder.

## Hierarchy
```
├── lib
└── src
    ├── application
    ├── controllers
    ├── models
    │   ├── profiles
    │   ├── services
    │   └── test
    ├── resources
    │   ├── icons
    │   ├── inventory
    │   ├── jpinyindb
    │   ├── profiles
    │   ├── questions
    │   └── styles
    ├── views
    ├── hanlp.properties
    └── resources.properties
```