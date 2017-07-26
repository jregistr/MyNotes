import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Item, TodoList} from "../../../models/models";
import {EventEmitterService} from "../../../services/event-emitter.service";
import {TodoListsService} from "../../../services/todo-lists.service";

@Component({
  selector: 'app-add-item-form',
  templateUrl: './add-item-form.component.html',
  styleUrls: ['./add-item-form.component.sass']
})
export class AddItemFormComponent implements OnInit {

  @Input() todoList: TodoList = null;

  addItemForm: FormGroup = new FormGroup({
    text: new FormControl(null, [Validators.required]),
    checked: new FormControl(false)
  });

  constructor(private service: TodoListsService) {
  }

  onFormSubmit(): void {
    if (this.addItemForm.valid) {
      const formValue = this.addItemForm.value;
      const item = new Item(this.todoList.id, formValue.text, false, -1);
      this.service.createItem(item).subscribe(item => {
        EventEmitterService.get("added-new-item").emit(item);
      });
    }
    EventEmitterService.get("add-item-end").emit(false);
  }

  onFormCancel(): void {
    EventEmitterService.get("add-item-end").emit(false);
  }

  ngOnInit() {
  }

}
