import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";
import {CustomizeService} from "../../../../services/customize.service";

@Component({
  selector: 'app-get-started',
  templateUrl: './embedded.component.html',
  styleUrls: ['./embedded.component.scss'],
})
export class EmbeddedComponent implements OnInit {
  thumbImage = '../../../../assets/images/embedded_pis_initiation_thumb.svg';
  fullImage = '../../../../assets/images/embedded_pis_initiation.svg';
  mode = 'hover-freeze';

  pathToEmbedded = `./assets/content/i18n/en/test-cases/embedded.md`;

  constructor(private languageService: LanguageService,
              private customizeService: CustomizeService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToEmbedded = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/embedded.md`;
      });
  }
}
