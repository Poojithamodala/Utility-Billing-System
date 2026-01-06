import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTariff } from './create-tariff';

describe('CreateTariff', () => {
  let component: CreateTariff;
  let fixture: ComponentFixture<CreateTariff>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTariff]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateTariff);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
