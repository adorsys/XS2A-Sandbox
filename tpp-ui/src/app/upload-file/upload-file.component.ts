import { Component, OnInit } from '@angular/core';
import {FileItem} from 'ng2-file-upload';
import {UploadOptions} from '../services/upload.service';

@Component({
  selector: 'app-upload-file',
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.css']
})
export class UploadFileComponent implements OnInit {

  public options: UploadOptions;
  constructor() { }

  public ngOnInit(): void {
    this.options = {
      method: options.method,
      url: options.url,
      maxFileSize: 100000, // 100 kb
      allowedMimeType: options.allowedMimeType,
      context: APP_CONSTANTS.UPLOAD.context.user,
      methodAfterSuccess: (item: FileItem, response: string) => {
        const globals: MpsGlobal = this.authService.getGlobalsObject();
        globals.currentUser.userInfo.logoProfile = JSON.parse(response) as Document;
        this.authService.saveGlobalsObject(globals);
        this.shareDataService.changeData({
          key: APP_CONSTANTS.GLOBALS_USER_INFO,
          value: globals
        });
        this.router.navigate(['users/all']);
      }
    };
  }

}
