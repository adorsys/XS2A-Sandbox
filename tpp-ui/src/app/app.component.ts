import {Component, OnInit} from '@angular/core';
import {IconRegistry} from "./commons/icon/icon-registry";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'app';
  urlPath = location.pathname;

  constructor(private _iconRegistry: IconRegistry, private _sanitizer: DomSanitizer) {
    const icons = ['user', 'account', 'upload', 'euro', 'add', 'generate_test_data'];
    icons.forEach(val => {
      _iconRegistry.addSvgIcon(
          val,
          _sanitizer.bypassSecurityTrustResourceUrl('assets/icons/'+val+'.svg')
      );
    });
  }

}
