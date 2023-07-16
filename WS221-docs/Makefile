RENDER=render
IMGDIR=images
PDFDIR=$(RENDER)/pdf
HTMLDIR=$(RENDER)/html
STYLE_PDF=redhat.yml
STYLE_HTML=custom.css

.PHONY: pdf html htmlimg clean all

htmlimg: $(HTMLDIR)/$(IMGDIR) $(addprefix $(HTMLDIR)/,$(wildcard images/*.png))

html: htmlimg $(RENDER)/custom.css $(patsubst %.adoc,$(HTMLDIR)/%.html,$(wildcard *.adoc))

pdf: $(RENDER)/custom.yml $(patsubst %.adoc,$(PDFDIR)/%.pdf,$(wildcard *.adoc))

all: html pdf

clean:
	rm -rf $(PDFDIR) $(HTMLDIR)

$(PDFDIR)/%.pdf: %.adoc
	asciidoctor-pdf -D $(PDFDIR)/ -a relfilesuffix=.pdf -a pdf-theme=$(RENDER)/$(STYLE_PDF) $<

$(HTMLDIR)/%.html: %.adoc
	asciidoctor -D $(HTMLDIR)/ -a relfilesuffix=.html -a stylesheet=$(RENDER)/$(STYLE_HTML) $<

$(HTMLDIR)/$(IMGDIR):
	mkdir -p $@

$(HTMLDIR)/$(IMGDIR)/%.png: $(IMGDIR)/%.png
	cp $< $@
