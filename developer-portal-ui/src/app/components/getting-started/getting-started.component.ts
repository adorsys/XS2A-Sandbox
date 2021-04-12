import { Component, OnInit, AfterViewChecked } from '@angular/core';
import { MarkdownStylingService } from '../../services/markdown-styling.service';
import { LanguageService } from '../../services/language.service';
import { CustomizeService } from '../../services/customize.service';
import { UrlService } from 'src/app/services/url.service';
import { EnvLink } from 'src/app/models/envLink.model';

@Component({
  selector: 'app-getting-started',
  templateUrl: './getting-started.component.html',
  styleUrls: ['./getting-started.component.scss'],
})
export class GettingStartedComponent implements OnInit, AfterViewChecked {
  pathToFile = './assets/content/i18n/en/getting-started.md';

  constructor(
    private markdownStylingService: MarkdownStylingService,
    private languageService: LanguageService,
    private customizeService: CustomizeService,
    private urlService: UrlService
  ) {}

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/getting-started.md`;
      this.markdownStylingService.createTableOfContent(true);
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
