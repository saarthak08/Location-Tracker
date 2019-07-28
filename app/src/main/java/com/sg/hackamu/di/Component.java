package com.sg.hackamu.di;

import com.sg.hackamu.utils.authentication.LoginHandler;
import com.sg.hackamu.utils.authentication.SignupHandler;
import com.sg.hackamu.view.faculties.FacultyLogin;
import com.sg.hackamu.viewmodel.repository.FacultyRepository;
import com.sg.hackamu.viewmodel.repository.StudentRepository;
import com.sg.hackamu.view.students.StudentMainActivity;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {ApplicationContextModule.class,FirebaseAuthModule.class})
public interface Component {

    void inject(StudentMainActivity studentMainActivity);

    void inject(LoginHandler loginHandler);

    void inject(StudentRepository studentRepository);

    void inject(FacultyRepository facultyRepository);

    void inject(FacultyLogin facultyLogin);

    void inject(SignupHandler signupHandler);
}
