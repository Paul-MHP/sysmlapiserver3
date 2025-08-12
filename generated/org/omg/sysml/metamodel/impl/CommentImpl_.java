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
@StaticMetamodel(CommentImpl.class)
public abstract class CommentImpl_ extends org.omg.sysml.lifecycle.impl.DataImpl_ {

	public static volatile ListAttribute<CommentImpl, Annotation> annotation;
	public static volatile SingularAttribute<CommentImpl, UUID> elementId;
	public static volatile ListAttribute<CommentImpl, TextualRepresentation> textualRepresentation;
	public static volatile SingularAttribute<CommentImpl, String> declaredShortName;
	public static volatile SingularAttribute<CommentImpl, Boolean> isLibraryElement;
	public static volatile ListAttribute<CommentImpl, Annotation> ownedAnnotation;
	public static volatile ListAttribute<CommentImpl, Element> ownedElement;
	public static volatile ListAttribute<CommentImpl, Annotation> ownedAnnotatingRelationship;
	public static volatile ListAttribute<CommentImpl, String> aliasIds;
	public static volatile ListAttribute<CommentImpl, Relationship> ownedRelationship;
	public static volatile SingularAttribute<CommentImpl, String> qualifiedName;
	public static volatile ListAttribute<CommentImpl, Documentation> documentation;
	public static volatile ListAttribute<CommentImpl, Element> annotatedElement;
	public static volatile SingularAttribute<CommentImpl, String> body;
	public static volatile SingularAttribute<CommentImpl, String> locale;
	public static volatile SingularAttribute<CommentImpl, Boolean> isImpliedIncluded;
	public static volatile SingularAttribute<CommentImpl, String> name;
	public static volatile SingularAttribute<CommentImpl, String> declaredName;
	public static volatile SingularAttribute<CommentImpl, String> shortName;

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

