package org.omg.sysml.metamodel.impl;

import java.util.UUID;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.omg.sysml.metamodel.Annotation;
import org.omg.sysml.metamodel.Documentation;
import org.omg.sysml.metamodel.Element;
import org.omg.sysml.metamodel.Relationship;
import org.omg.sysml.metamodel.TextualRepresentation;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DocumentationImpl.class)
public abstract class DocumentationImpl_ extends org.omg.sysml.lifecycle.impl.DataImpl_ {

	public static volatile ListAttribute<DocumentationImpl, Annotation> annotation;
	public static volatile SingularAttribute<DocumentationImpl, UUID> elementId;
	public static volatile ListAttribute<DocumentationImpl, TextualRepresentation> textualRepresentation;
	public static volatile SingularAttribute<DocumentationImpl, String> declaredShortName;
	public static volatile SingularAttribute<DocumentationImpl, Boolean> isLibraryElement;
	public static volatile ListAttribute<DocumentationImpl, Annotation> ownedAnnotation;
	public static volatile ListAttribute<DocumentationImpl, Element> ownedElement;
	public static volatile ListAttribute<DocumentationImpl, Annotation> ownedAnnotatingRelationship;
	public static volatile ListAttribute<DocumentationImpl, String> aliasIds;
	public static volatile ListAttribute<DocumentationImpl, Relationship> ownedRelationship;
	public static volatile SingularAttribute<DocumentationImpl, String> qualifiedName;
	public static volatile ListAttribute<DocumentationImpl, Documentation> documentation;
	public static volatile ListAttribute<DocumentationImpl, Element> annotatedElement;
	public static volatile SingularAttribute<DocumentationImpl, String> body;
	public static volatile SingularAttribute<DocumentationImpl, String> locale;
	public static volatile SingularAttribute<DocumentationImpl, Boolean> isImpliedIncluded;
	public static volatile SingularAttribute<DocumentationImpl, String> name;
	public static volatile SingularAttribute<DocumentationImpl, String> declaredName;
	public static volatile SingularAttribute<DocumentationImpl, String> shortName;

	public static final String ANNOTATION = "annotation";
	public static final String ELEMENT_ID = "elementId";
	public static final String TEXTUAL_REPRESENTATION = "textualRepresentation";
	public static final String DECLARED_SHORT_NAME = "declaredShortName";
	public static final String IS_LIBRARY_ELEMENT = "isLibraryElement";
	public static final String OWNED_ANNOTATION = "ownedAnnotation";
	public static final String OWNED_ELEMENT = "ownedElement";
	public static final String OWNED_ANNOTATING_RELATIONSHIP = "ownedAnnotatingRelationship";
	public static final String ALIAS_IDS = "aliasIds";
	public static final String OWNED_RELATIONSHIP = "ownedRelationship";
	public static final String QUALIFIED_NAME = "qualifiedName";
	public static final String DOCUMENTATION = "documentation";
	public static final String ANNOTATED_ELEMENT = "annotatedElement";
	public static final String BODY = "body";
	public static final String LOCALE = "locale";
	public static final String IS_IMPLIED_INCLUDED = "isImpliedIncluded";
	public static final String NAME = "name";
	public static final String DECLARED_NAME = "declaredName";
	public static final String SHORT_NAME = "shortName";

}

