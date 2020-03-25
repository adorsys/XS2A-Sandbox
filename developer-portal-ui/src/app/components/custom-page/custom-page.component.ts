import {Component, DoCheck} from '@angular/core';
import {LanguageService} from "../../services/language.service";
import {ActivatedRoute} from "@angular/router";
import {CustomizeService} from "../../services/customize.service";

@Component({
  selector: 'app-custom-page',
  templateUrl: './custom-page.component.html',
  styleUrls: ['./custom-page.component.scss']
})
export class CustomPageComponent implements DoCheck {
  private name = 'faq';
  pathToFile = `./assets/content/i18n/en/${this.name}.md`;

  constructor(private languageService: LanguageService,
              private route: ActivatedRoute,
              private customizeService: CustomizeService) {
  }

  ngDoCheck() {
    this.name = this.route.snapshot.paramMap.get('name');
    if (this.name) {
      this.languageService.currentLanguage.subscribe(
        data => {
          this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/${this.name}.md`;
        }
      );
    }
  }
}
