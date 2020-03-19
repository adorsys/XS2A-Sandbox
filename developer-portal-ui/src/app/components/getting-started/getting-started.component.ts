import {Component, OnInit} from '@angular/core';
import {MarkdownStylingService} from '../../services/markdown-styling.service';
import {LanguageService} from "../../services/language.service";

@Component({
  selector: 'app-getting-started',
  templateUrl: './getting-started.component.html',
  styleUrls: ['./getting-started.component.scss'],
})
export class GettingStartedComponent implements OnInit {
  pathToFile = './assets/i18n/en/getting-started.md';

  constructor(private markdownStylingService: MarkdownStylingService,
              private languageService: LanguageService) {
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToFile = `./assets/i18n/${data}/getting-started.md`;

        this.markdownStylingService.createTableOfContent(true);
      }
    );
  }
}
