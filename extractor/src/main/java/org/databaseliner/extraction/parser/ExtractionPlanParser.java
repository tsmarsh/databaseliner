package org.databaseliner.extraction.parser;

import java.util.ArrayList;
import java.util.List;

import org.databaseliner.database.DatabaseConnector;
import org.databaseliner.extraction.ExtractionType;
import org.databaseliner.extraction.Relationship;
import org.databaseliner.extraction.RelationshipType;
import org.databaseliner.extraction.SeedExtraction;
import org.databaseliner.extraction.model.ExtractionModel;
import org.dom4j.Document;
import org.dom4j.Node;

public class ExtractionPlanParser {
	
	public static ExtractionModel parse(Document configDocument, DatabaseConnector databaseConnector) {
		
		List<SeedExtraction> seeds = parseSeedExtractions(configDocument);
		List<String> ignoredTableNames = parseIgnoredTables(configDocument);
		List<Relationship> relationships = parseRelationships(configDocument);
		
		boolean dryRunMode = parseDryRunModeSetting(configDocument);
		
		ExtractionModel extractionModel = new ExtractionModel(databaseConnector, seeds, ignoredTableNames, relationships, dryRunMode);
		extractionModel.build();
		
		return extractionModel;
	}

	private static boolean parseDryRunModeSetting(Document configDocument) {
		Node dryRunNode = configDocument.selectSingleNode("//databaseliner/extractionPlan/@dryRun");
		if (dryRunNode != null) {
			return Boolean.parseBoolean(dryRunNode.getText());
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static List<SeedExtraction> parseSeedExtractions(Document configDocument) {

		List<SeedExtraction> seeds = new ArrayList<SeedExtraction>();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/extractionPlan/extraction");
		for (Node extractionNode : selectNodes) {
			seeds.add(ExtractionType.getExtractionForNode(extractionNode));
		}
		
		return seeds;
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> parseIgnoredTables(Document configDocument) {
		
		List<String> ignoredTableNames = new ArrayList<String>();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/extractionPlan/ignore");
		for (Node node : selectNodes) {
			ignoredTableNames.add(node.selectSingleNode("@table").getText().trim());
		}
		return ignoredTableNames;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Relationship> parseRelationships(Document configDocument) {
		List<Relationship> relationships = new ArrayList<Relationship>();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/extractionPlan/relationship");
		for (Node extractionNode : selectNodes) {
			relationships.add(RelationshipType.getRelationshipForNode(extractionNode));
		}
		
		return relationships;
	}

}
