package edu.uah.rsesc.agree.ge.businessObjectHandlers;

import javax.inject.Named;

import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.BooleanLiteral;
import org.osate.aadl2.ComponentImplementation;
import org.osate.ge.BusinessObjectContext;
import org.osate.ge.GraphicalConfiguration;
import org.osate.ge.GraphicalConfigurationBuilder;
import org.osate.ge.GraphicalExtensionUtil;
import org.osate.ge.PaletteEntry;
import org.osate.ge.PaletteEntryBuilder;
import org.osate.ge.di.BuildCreateOperation;
import org.osate.ge.di.CanCreate;
import org.osate.ge.di.CanDelete;
import org.osate.ge.di.GetGraphicalConfiguration;
import org.osate.ge.di.GetName;
import org.osate.ge.di.GetNameForEditing;
import org.osate.ge.di.GetPaletteEntries;
import org.osate.ge.di.IsApplicable;
import org.osate.ge.di.Names;
import org.osate.ge.di.Rename;
import org.osate.ge.di.ValidateName;
import org.osate.ge.graphics.Graphic;
import org.osate.ge.graphics.RectangleBuilder;
import org.osate.ge.graphics.Style;
import org.osate.ge.graphics.StyleBuilder;
import org.osate.ge.operations.Operation;
import org.osate.ge.operations.OperationBuilderHelper;
import org.osate.ge.operations.StepResultBuilder;

import com.rockwellcollins.atc.agree.agree.AgreeContract;
import com.rockwellcollins.atc.agree.agree.AgreeFactory;
import com.rockwellcollins.atc.agree.agree.BoolLitExpr;
import com.rockwellcollins.atc.agree.agree.LemmaStatement;

import edu.uah.rsesc.agree.ge.AgreeCategories;

public class LemmaStatementHandler {
	private static final Graphic graphic = RectangleBuilder.create().build();

	@IsApplicable
	@CanDelete
	public boolean isApplicable(final @Named(Names.BUSINESS_OBJECT) LemmaStatement bo) {
		return true;
	}

	@GetGraphicalConfiguration
	public GraphicalConfiguration getGraphicalConfiguration(
			final @Named(Names.BUSINESS_OBJECT_CONTEXT) BusinessObjectContext boc) {
		return GraphicalConfigurationBuilder.create().graphic(graphic).annotation("<lemma>").style(StyleBuilder.create(
				GraphicalExtensionUtil.isInherited(boc) ? GraphicalExtensionUtil.STYLE_INHERITED_ELEMENT : Style.EMPTY)
				.labelsTop().labelsHorizontalCenter().build()).build();
	}

	@GetPaletteEntries
	public PaletteEntry[] getPaletteEntries(final @Named(Names.DIAGRAM_BO) Object diagramBo) {
		if (!AgreeBusinessObjectHandlerUtil.shouldContributePaletteEntries(diagramBo)) {
			return null;
		}

		return new PaletteEntry[] {
				PaletteEntryBuilder.create().label("Lemma").category(AgreeCategories.AGREE).build() };
	}

	private static OperationBuilderHelper<ComponentImplementation> getClassifierOpBuilder() {
		return OperationBuilderHelper.componentImplementations().allowAnyMatchingClassifier();
	}

	@CanCreate
	public boolean canCreate(final @Named(Names.TARGET_BO) Object bo) {
		return getClassifierOpBuilder().canBuildOperation(bo);
	}

	@BuildCreateOperation
	public Operation buildCreateOperation(
			final @Named(Names.TARGET_BUSINESS_OBJECT_CONTEXT) BusinessObjectContext targetBoc) {
		return Operation.create(createOp -> {
			getClassifierOpBuilder().buildOperation(createOp, targetBoc.getBusinessObject())
					.map(AgreeHandlerUtil.toBusinessObjectToModify()).modifyPreviousResult(modifyBo -> {
						final AgreeContract agreeContract = AgreeHandlerUtil.getOrCreateAgreeContract(modifyBo);
						final LemmaStatement newBo = AgreeFactory.eINSTANCE.createLemmaStatement();
						newBo.setStr("New Lemma");
						final BoolLitExpr trueExpr = AgreeFactory.eINSTANCE.createBoolLitExpr();
						final BooleanLiteral trueLit = Aadl2Factory.eINSTANCE.createBooleanLiteral();
						trueLit.setValue(true);
						trueExpr.setVal(trueLit);
						newBo.setExpr(trueExpr);
						agreeContract.getSpecs().add(newBo);

						return StepResultBuilder.create(newBo).showNewBusinessObject(targetBoc, newBo).build();
					});
		});
	}

	@GetName
	public String getName(final @Named(Names.BUSINESS_OBJECT) LemmaStatement bo) {
		return AgreeHandlerUtil.toDisplayName(bo.getStr());
	}

	@GetNameForEditing
	public String getNameForEditing(final @Named(Names.BUSINESS_OBJECT) LemmaStatement bo) {
		return bo.getStr();
	}

	@ValidateName
	public String validateName(final @Named(Names.BUSINESS_OBJECT) LemmaStatement bo,
			final @Named(Names.NAME) String value) {
		// Any name is valid
		return null;
	}

	@Rename
	public void setName(final @Named(Names.BUSINESS_OBJECT) LemmaStatement bo, final @Named(Names.NAME) String value) {
		bo.setStr(value);
	}
}
