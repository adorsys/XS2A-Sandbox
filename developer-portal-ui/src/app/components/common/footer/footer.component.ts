import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { CustomizeService } from '../../../services/customize.service';
import { MarkdownStylingService } from '../../../services/markdown-styling.service';
import { LanguageService } from '../../../services/language.service';
import { UrlService } from 'src/app/services/url.service';
import { EnvLink } from 'src/app/models/envLink.model';
import { MarkdownService } from 'ngx-markdown';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit, AfterViewChecked {
  pathToFile = `./assets/content/i18n/en/footer.md`;
  compiledMarkDown = '';

  constructor(
    private customizeService: CustomizeService,
    private markdownStylingService: MarkdownStylingService,
    private languageService: LanguageService,
    private urlService: UrlService,
    private markdownService: MarkdownService
  ) {}

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/footer.md`;
      this.markdownStylingService.createTableOfContent(true);
      this.markdownService.getSource(this.pathToFile).subscribe((result) => {
        this.compiledMarkDown = this.markdownService.compile(result);
      });
    });
  }

  ngAfterViewChecked() {
    this.urlService.getUrl().subscribe((data: EnvLink) => {
      Object.keys(data.servicesAvailable).forEach((key) => this.setLink(key, data.servicesAvailable[key].environmentLink));
    });
  }

  setLink(id: string, link: string) {
    const anchorNode = document.getElementById(id);
    if (anchorNode) {
      anchorNode.setAttribute('href', link);
    }
  }
}
