import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-welcome',
  templateUrl: './redirect.component.html',
  styleUrls: ['./redirect.component.scss'],
})
export class RedirectComponent implements OnInit{
  thumbImage = '../../../../assets/images/redirect_pis_initiation_thumb.svg';
  fullImage = '../../../../assets/images/redirect_pis_initiation.svg';
  mode = 'hover-freeze';

  pathToRedirect = `./assets/i18n/en/test-cases/redirect.md`;

  constructor(private languageService: LanguageService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToRedirect = `./assets/i18n/${data}/test-cases/redirect.md`;
      });
  }
}
