package org.planqk.library.rest.model;

import org.jabref.model.study.Study;

public class StudyDTO {
    public Study studyDefinition;

    public StudyDTO() {

    }

    public StudyDTO(Study studyDefinition) {
        this.studyDefinition = studyDefinition;
    }
}
