import { Config } from '../models/config';
import { ConfigService } from './common/services/config.service';

export function initializer(configService: ConfigService): () => Promise<any> {
  return (): Promise<any> =>
    configService
      .loadConfig()
      .then((config: Config) => {
        configService.setConfig(config);
      })
      .catch(err => {});
}
