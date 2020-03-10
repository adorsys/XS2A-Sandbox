import {Component} from '@angular/core';
import {LanguageService} from "../../../../services/language.service";

@Component({
  selector: 'app-postman-testing',
  templateUrl: './postman-testing.component.html',
  styleUrls: ['./postman-testing.component.scss'],
})
export class PostmanTestingComponent {
  pathToPostman = `./assets/i18n/en/test-cases/postman.md`;

  constructor(private languageService: LanguageService) {
  }

  ngOnInit(): void {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToPostman = `./assets/i18n/${data}/test-cases/postman.md`;
      });
  }
}
