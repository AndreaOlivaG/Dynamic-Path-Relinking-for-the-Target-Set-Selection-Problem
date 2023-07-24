![visitor badge](https://vbr.wocr.tk/badge?page_id=AndreaOlivaG.Dynamic-Path-Relinking-for-the-Target-Set-Selection-Problem&color=be54c6&style=flat&logo=Github)
![Manintained](https://img.shields.io/badge/Maintained%3F-yes-green.svg)
![GitHub last commit (master)](https://img.shields.io/github/last-commit/AndreaOlivaG/Dynamic-Path-Relinking-for-the-Target-Set-Selection-Problem)
![Starts](https://img.shields.io/github/stars/AndreaOlivaG/Dynamic-Path-Relinking-for-the-Target-Set-Selection-Problem.svg)

# Dynamic Path Relinking for the Target Set Selection Problem

This research proposes the use of metaheuristics for solving the Target Set Selection (TSS) problem. This problem emerges in the context of influence maximization problems, in which the objective is to maximize the number of active users when spreading information throughout a social network. Among all the influence maximization variants, TSS introduces the concept of reward of each user, which is the benefit associated to its activation. Therefore, the problem tries to maximize the reward obtained among all active users by selecting an initial set of users. Each user has also associated an activation cost, and the total sum of activation costs of the initial set of selected users cannot exceed a certain budget. In particular, two Path Relinking approaches are proposed, comparing them with the best method found in the state of the art. Additionally, a more challenging set of instances are derived from real-life social networks, where the best previous method is not able to find a feasible solution. The experimental results show the efficiency and efficacy of the proposal, supported by non-parametric statistical tests.

- Paper link: [TODO](TODO) <br>
- DOI: [TODO](TODO) <br>
- Impact Factor: 8.800 (2022)
- Quartil: Q1 - 10/145 - Computer Science, Artificial Intelligence | 2022  <br>
- Journal: Knowledge-Based Systems

## Datasets

* [1-83]: Same repository as the original dataset,  (http://vlado.fmf.uni-lj.si/pub/networks/data/UciNet/UciData.htm).
* [84-89]: SNAP dataset, (https://snap.stanford.edu/data/).
* [90]: BlogCatalog, (http://datasets.syr.edu/datasets/BlogCatalog3.html).

The instances can be found in the instances folder (some of the instances are compressed with .7z because they are space demanding).

## Source code

The code´s folder contains the proyect with our algorithmic proposal. We use IntellIj IDE, the proyect can be build with maven, we also add the required java libraries.


## Results

The following excel document contains all the final results divided by instance. It also contains the final tables and the network information table.

* Tables.xlsx


## Executable

You can just run the TSS.jar as follows.

```
java -jar TSS.jar <instances_folder> <eliteSet> <initialPopulation>
```

For instance:

```
java -jar TSS.jar "./instances"
java -jar TSS.jar "instances" 20 10
```

- instances folder per default is ../instances/ 
- eliteSet per default is 10.
- initialPopulation per default is 20.

## Instances Generator

We would like to thank the authors for kindly sending us the code Santiago V. Ravelo and Claudio N. Meneses to calculate all the required parameters to generate the instance. Following this definition, the users with larger influence has a larger associated reward. 

Code can be found in instancesGenerator folder.


## Cite

Please cite our paper if you use it in your own work:

Bibtext
```
TODO
```

RIS
```
TODO
```

AMA Style
```
TODO
```

Chicago/Turabian Style
```
TODO
```
