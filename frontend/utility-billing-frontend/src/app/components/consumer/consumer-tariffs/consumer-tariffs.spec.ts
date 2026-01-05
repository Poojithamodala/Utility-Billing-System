import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumerTariffs } from './consumer-tariffs';

describe('ConsumerTariffs', () => {
  let component: ConsumerTariffs;
  let fixture: ComponentFixture<ConsumerTariffs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsumerTariffs]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumerTariffs);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
