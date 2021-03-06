package net.warpgame.engine.postbuild.filters;

import net.warpgame.engine.postbuild.processing.Context;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Jaca777
 * Created 2018-07-01 at 17
 */
public class AnnotatedClassesFilter extends ClassFilter {

    private String annotationName;

    public AnnotatedClassesFilter(String annotationName) {
        this.annotationName = annotationName;
    }

    @Override
    protected boolean passesFilter(ClassNode classNode, Context c) {
        return classNode.visibleAnnotations.stream()
                .anyMatch(ann -> ann.desc.equals(annotationName));
    }
}
