import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";
import {CustomizeService} from "../../../../services/customize.service";

@Component({
  selector: 'app-welcome',
  templateUrl: './redirect.component.html',
  styleUrls: ['./redirect.component.scss'],
})
export class RedirectComponent implements OnInit{
  thumbImage = '../../../../assets/images/redirect_pis_initiation_thumb.svg';
  fullImage = '../../../../assets/images/redirect_pis_initiation.svg';
  mode = 'hover-freeze';

  pathToRedirect = `./assets/content/i18n/en/test-cases/redirect.md`;

  constructor(private languageService: LanguageService,
              private customizeService: CustomizeService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToRedirect = `${this.customizeService.currentLanguageFolder}/${data}/test-cases/redirect.md`;
      });
  }
}
