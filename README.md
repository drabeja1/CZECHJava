# CZECHJava #

ČeskáJava (CZECHJava) je počeštění známého programovacího jazyka Java, tedy, přináší lepší srozumitelnost pro běžného česky mluvícího člověka. 

## Syntaxe ##

### Příklad Hello World ###
Jednoduchá ukázka Hello world v CZECHJavě.
```java
startuj proved

trida Udelatko {
  provadej() {
		Konzole.pis(“Hello world!”) proved
  }
}
```

* Všechny soubory začínají klíčovým slovem **startuj**
* Všechny soubory končí klíčovým slovem **konci**
* Každý statement programu musí být ukončen klíčovým slovem **proved** - nahrazuje středník (;)
* Hlavní třída programu se vždy jmenuje **Udelatko** a hlavní metoda se vždy jmenuje **provadej**
* Pozn.: CZECHJava je *case in-sensitive* a nedoporučuje se používat diakritiku

### Datové typy ###
V CZECHJAVA nalezneme 4 primitivní typy:
*	**cislo** – integer
* **jeto** – boolean
*	**pismenko** – char
*	**desetinne** – float

Hodnoty:
*	**jetotak** – true
*	**nenitotak** – false
*	**nictam** – null

Základní knihovna také obsahuje třídy:
*	**HodnePismenek** – ekvivalent String,
*	**Perpetummobile** – všechny třídy dědí implicitně od této (náhrada Object). Obsahuje metody **naHodnePismenek()** a **jsouStejny(Perpetummobile p)**, ekvivalenty pro toString() a equals(Object o)
*	**CistSoubor** – čtení ze souboru
*	**Konzole** – výpis do konzole
*	**Bufr** – jednoduchý buffer na čtení písmenek

### Deklarace ###
Deklaraci je nutno začít klíčovým slovem *necht*

```java
necht cislo i = 1 proved
```

### Podmínky ###
Pro vytvoření podmínky použijeme konstrukt **plati – neboi - jinak**.
Jednotlivé výrazy můžeme spojovat pomocí booleovských operátorů **taky** (and) a **nebo** (or)

```java
necht jeto a proved
necht jeto b proved
// …
plati (a == jetotak nebo b == jetotak) {
	// …
} neboi (a == nenitotak taky b == jetotak) {
	// …
} jinak {
  // …
}
```

### Cykly ###
V CZECHJava se vyskytuje pouze jden cyklus - **opakuj**, reprezentuje while.
Opakuj cyklus se dá přerušit pomocí **stuj** (break), a nebo přeskočit jeden cyklus pomocí **jdidal** (continue).

#### Příklad 2 ####
```java
startuj proved

trida Priklad2 {
	nacti (HodnePismenek nazevSouboru) {
		necht CistSoubor c = vytvor CistSoubor() proved
		c.otevri(nazevSouboru) proved

		necht HodnePismenek radka proved
		radka = c.ctiRadek() proved
		
		opakuj (radka != nictam) {
			// …
			radka = c.ctiRadek() proved
		}
		c.zavri() proved
	}
}

konci proved
```

### Děičnost ###
CZECHJava je jazyk objektový, pokud chceme uvést třídu, ze které chceme dědi, použijeme konstrukt **dedic**. 
Jako reference na vlastní instanci slouží konstrukt **toto** (ekvivalent this). Reference na rodiče je zde jednoduše **rodic** (ekvivalent super)
Nový objekt vytvoříme pomocí **vytvor** (ekvivalent new).

### Příklad dědičnosti ###
```java
startuj proved

trida Obdelnik {
	necht cislo delka proved
	necht cislo vyska proved

	Obdelnik(cislo delka, cislo vyska) {
		toto.delka = delka proved
		toto.vyska = vyska proved
  }
}	

trida Ctverec dedic Obdelnik {
	Ctverec(cislo strana) {
		rodic(strana, strana) proved
	}
}

trida Udelatko {
  provadej() {
		necht Ctverec c = vytvor Ctverec(5) proved
  }
}

konci proved
```

### Pole ###
Příklad vytvoření pole:
```java
necht cislo[] pole = vytvor cislo[5] proved
```

### Metody ###
Mimo standardní Javu je zde jediný rozdíl a to že pro metodu s prázdným návratovým typem (void) není třeba psát nic!
Jako return statement zde slouží **vrat**.

```java
trida Foo {
	cislo foo(HodnePismenek txt) {
		vrat txt.delka proved
	}
}
```

## Implementace ##

V aktuální verzi CZECHJava je implementováno:

### Compiler ###
Compiler podporuje:
* Typovou kontrolu - přiřazení objektů, navrátové hodnoty funkcí, fieldy
* Kontrolu deklarace a inicializace proměnných
* Kontrolu volání metod (static, non-static)

### Interpreter ###
Realizace interpreteru:
* Paměť pomocí pole objektů
* Generační garbage collecor
* Nativní a statické volání
* Primitiva (integer a float) na stacku a pointer s 1 na posledním bitu
* Aritmetické operace pro int a float
* Pole - priitivní i referencí
* Dynamický lookup metod
* Overload metod

### Parser ###
Parser je vygenerován pomocí nástroje javacc 5.0.

## Kompilace a spuštění ##
Pro kompilaci CZECHJava překladaše je potřeba:
* JDK 1.8,
* Maven2

V případě spouštění zkompilovaných binárek postačí JRE 1.8.

Projekt je veden vytvořen v Netbeans 8.

### Kompilace ###
Pomocí Netbeans, nebo v kořenové složce:
```
mvn clean
mvn install
```

### Kompilace CZECHJava programů ###
Pro kompilaci programů je určen compiler **czechjavac**, který má dva argumenty:
* -s - zdrojový soubor, nebo složka se zdrojovými soubory,
* -t - cílová složka, do které budou umístěny kompilované soubory

Výchozím typem CZECHJava souborů je typ **.czj**

Příklad:
```
./czechjavac -s examples/Prints/ -t compiled/
```

### Spouštění CZECHJava programů ###
Pro spoštění zkompilovaných souborů slouží interptreter **czechjava**. Ten přijímá argumenty:
* -h - velikost heapu (volitelně),
* -f - pocet framu (volitelně),
* -s - velikost stacku ve framu (volitelně),
* -c - složka s kompilovanými soubory,
* -a - výčet argumentů programu (volitelně).

Příklad:
```
./czechjava -h 1024 -f 256 -s 128 -c compiled/ -a test
```