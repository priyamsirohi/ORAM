name=oram

all: clean
	latex $(name).tex
	bibtex $(name)
	latex $(name).tex
	latex $(name).tex
	dvips -t letter -Pcmz $(name).dvi -o $(name).ps
	ps2pdf $(name).ps & 


clean:
	rm -rf *~ *.dvi *.blg *.log .*~ *.aux *.ps *.bbl *.pdf

pdf: 
	ps2pdf -dPDFSETTINGS=/prepress -dSubsetFonts=true -dEmbedAllFonts=true -dMaxSubsetPct=100 -dCompatibilityLevel=1.3 pd.ps 

view:
	okular $(name).pdf &

	
