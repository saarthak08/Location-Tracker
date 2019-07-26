package com.sg.hackamu.di;

import com.sg.hackamu.authentication.LoginHandler;
import com.sg.hackamu.faculties.FacultyLogin;
import com.sg.hackamu.repository.FacultyRepository;
import com.sg.hackamu.repository.StudentRepository;
import com.sg.hackamu.students.StudentMainActivity;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {ApplicationContextModule.class,FirebaseAuthModule.class})
public interface Component {

    void inject(StudentMainActivity studentMainActivity);

    void inject(LoginHandler loginHandler);

    void inject(StudentRepository studentRepository);

    void inject(FacultyRepository facultyRepository);

    void inject(FacultyLogin facultyLogin);
}
