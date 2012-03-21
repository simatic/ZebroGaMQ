# TCM: TOTEM Communication Middleware
# Copyright: Copyright (C) 2009-2012
# Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu
# Permission is granted to copy, distribute and/or modify this document
# under the terms of the GNU Free Documentation License, Version 1.3
# or any later version published by the Free Software Foundation;
# with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
# A copy of the license is included in the section entitled "GNU
# Free Documentation License".

BASE	= document.tex
SOURCES = $(wildcard *.tex)
BIBS	= $(wildcard *.bib)
FIGURES	= $(wildcard Figures/*.fig)
PS	= $(BASE:.tex=.ps)
PDF	= $(BASE:.tex=.pdf)

all:	ps pdf

cleanall: clean
	rm -f *.ps *.pdf *.tgz

clean:
	rm -f *.log *.aux *.bbl *.blg *~ *.bak *.dvi *.out \
	images/[!_]*.eps images/*.png [!_]*.eps *.png \
	*.toc *.lof *.lot *.backup Figures/*.bak

ps:	$(BASE:%.tex=%.ps)

%.ps:	$(SOURCES) $(BIBS) $(FIGURES:%.fig=%.eps)
	latex  $*.tex
# mettre un tiret devant bibtex au cas où il n'y a pas de bib
	- bibtex $*
	latex  $*.tex
	latex  $*.tex
	latex  $*.tex
	dvips -P hp -o $*.ps $*.dvi

pdf:	$(BASE:%.tex=%.pdf)

%.pdf:	$(SOURCES) $(BIBS) $(FIGURES:%.fig=%.eps)
	latex  $*.tex
# mettre un tiret devant bibtex au cas où il n'y a pas de bib
	- bibtex $*
	latex  $*.tex
	latex  $*.tex
	latex  $*.tex
	dvipdfm -o $*.pdf $*.dvi


%.eps: %.fig
	fig2dev -L eps $< $@

%.png: %.fig
	fig2dev -L png $< $@

tgz:	$(BASE:%.tex=%.tgz)

%.tgz : $(SOURCES) %.tex clean
	 tar zcvf $*.tgz * --exclude Inputs --exclude *.tgz

