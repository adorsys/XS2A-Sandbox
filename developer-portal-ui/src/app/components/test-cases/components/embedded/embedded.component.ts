import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-get-started',
  templateUrl: './embedded.component.html',
  styleUrls: ['./embedded.component.scss'],
})
export class EmbeddedComponent implements OnInit {
  thumbImage = '../../../../assets/images/embedded_pis_initiation_thumb.svg';
  fullImage = '../../../../assets/images/embedded_pis_initiation.svg';
  mode = 'hover-freeze';

  pathToEmbedded = `./assets/i18n/en/test-cases/embedded.md`;

  constructor(private languageService: LanguageService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToEmbedded = `./assets/i18n/${data}/test-cases/embedded.md`;
      });
  }
}
