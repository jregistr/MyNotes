import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {EventEmitterService} from "../../../../services/event-emitter.service";
import {TodoList} from "../../../../models/models";
import {TodoListsService} from "../../../../services/todo-lists.service";

@Component({
  selector: 'app-make-todo-form',
  templateUrl: './make-todo-form.component.html',
  styleUrls: ['./make-todo-form.component.sass']
})
export class MakeTodoFormComponent implements OnInit {

  constructor(private listService: TodoListsService) {
  }

  makeTodoListForm: FormGroup = new FormGroup({
    name: new FormControl(null, [Validators.required, Validators.maxLength(50)]),
    description: new FormControl(null, [Validators.maxLength(100)]),
    userId: new FormControl(1, [Validators.required])
  });

  ngOnInit() {
  }

  onFormSubmit(): void {
    if (this.makeTodoListForm.valid) {
      const formValue = this.makeTodoListForm.value;
      const todoList = new TodoList(formValue.userId, formValue.name, formValue.description, null, -1);
      this.listService.createTodoList(todoList).subscribe(todoList => {
        EventEmitterService.get("new-todo-came").emit(todoList);
      });
    }
    EventEmitterService.get("Create-Todo-Done").emit(true);
  }

  onFormCancel(): void {
    EventEmitterService.get("Create-Todo-Done").emit(false);
  }

}
