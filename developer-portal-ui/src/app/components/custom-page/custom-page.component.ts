import {Component, DoCheck} from '@angular/core';
import {LanguageService} from "../../services/language.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-custom-page',
  templateUrl: './custom-page.component.html',
  styleUrls: ['./custom-page.component.scss']
})
export class CustomPageComponent implements DoCheck {
  private name = 'faq';
  pathToFile = `./assets/i18n/en/${this.name}.md`;

  constructor(private languageService: LanguageService,
              private route: ActivatedRoute) {
  }

  ngDoCheck() {
    this.name = this.route.snapshot.paramMap.get('name');
    if (this.name) {
      this.languageService.currentLanguage.subscribe(
        data => {
          this.pathToFile = `./assets/i18n/${data}/${this.name}.md`;
        }
      );
    }
  }
}
