import { Component, OnInit } from '@angular/core';
import { CustomizeService } from '../../../services/customize.service';
import { MarkdownStylingService } from '../../../services/markdown-styling.service';
import { LanguageService } from '../../../services/language.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  pathToFile = `./assets/content/i18n/en/footer.md`;

  constructor(
    private customizeService: CustomizeService,
    private markdownStylingService: MarkdownStylingService,
    private languageService: LanguageService
  ) {}

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/footer.md`;
      this.markdownStylingService.createTableOfContent(true);
    });
  }
}
