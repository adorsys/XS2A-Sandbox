import {Component, OnInit} from '@angular/core';
import {MarkdownStylingService} from '../../services/markdown-styling.service';
import {LanguageService} from "../../services/language.service";
import {CustomizeService} from "../../services/customize.service";

@Component({
  selector: 'app-getting-started',
  templateUrl: './getting-started.component.html',
  styleUrls: ['./getting-started.component.scss'],
})
export class GettingStartedComponent implements OnInit {
  pathToFile = './assets/content/i18n/en/getting-started.md';

  constructor(private markdownStylingService: MarkdownStylingService,
              private languageService: LanguageService,
              private customizeService: CustomizeService) {
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/getting-started.md`;

        this.markdownStylingService.createTableOfContent(true);
      }
    );
  }
}
