import { PrettyJsonPipe } from './pretty-json.pipe';

describe('PrettyJsonPipe', () => {
  let pipe: PrettyJsonPipe;

  it('create an instance', () => {
    pipe = new PrettyJsonPipe();
    expect(pipe).toBeTruthy();
  });
});
